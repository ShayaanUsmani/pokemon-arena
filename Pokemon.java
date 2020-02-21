import java.util.*;
public class Pokemon {
    private String name; // name of pokemon
    private int hp; // hit points of pokemon
    private String type; // type of pokemon
    private String resist; // what type pokemon it takes only half damage from
    private String weak; // what type pokemon it takes double damage from
    private int numAttacks; // number of attacks
    private int totEnergy;
    private Attack []attacks; //holds attack(s) of pokemon
    private int fullHp;

    private static int dmgDealt;  // DAMAGE DEALT BY A POKEMON TO A POKEMON
    static boolean callUI = true; // ENABLES UI TO BE CALLED/USED
    private static boolean disableUsedByComp = false; // HAS THE COMPUTER/ENEMY POKEMON USED THE DISABLE AFFECT?
    private static boolean disableUsedByPlayer = false; // HAS THE PLAYER/ALLY POKEMON USED THE DISABLE AFFECT?
    private static int disableReduceDmg = 10; // THE AMOUNT OF DAMAGE "DISABLE" REDUCES FROM ALL VERSING POKEMON
    private static int rechargeAmount = 20;

    private static int attackMove = 1; //we will refer to attack as 1,
    private static int retreatMove = 2; // retreat as 2
    private static int passMove = 3; // and pass as 3. This will be useful since we will require integer inputs from the user

    private static int evenNumber = 2;  // when the turn number is even, it is the player's turn
    private static int oddNumber = 1; // when the turn num is odd, it's the computer's turn




    //TODO:

    public Pokemon(String line) { // Pokemon object constructor
        String[] stats = line.split(","); //commas r separating diff stats of the line
        name = stats[0].toLowerCase(); // will also make input lowercase. easier to handle strings this way.
        hp = Integer.parseInt(stats[1]);
        fullHp = hp;
        type = stats[2];
        resist = stats[3];
        weak = stats[4];
        numAttacks = Integer.parseInt(stats[5]);
        totEnergy = 50;

        attacks = new Attack[numAttacks]; //array of all attacks of this pokemon

        for (int i = 0; i < numAttacks; i++) {    // iterate after all info for 1 attack is used
            String atkName = stats[6 + i * 4]; // we start at index 6 of the line since that is when the actual statistics of the attacks begin
            int atkDmg = Integer.parseInt(stats[6 + i * 4 + 1]); //+1 at end since we move to next index
            int atkCost = Integer.parseInt(stats[6 + i * 4 + 2]);
            String atkSpcl = stats[6 + i * 4 + 3];
            attacks[i] = new Attack(atkName, atkCost, atkDmg, atkSpcl);
        }
    }



    public static void dealDamage(Pokemon attacker, Pokemon victim, Attack atk){ // need attacker for type, victim for hp & type, atk for
                                                                                 // attack name and energy
        if(attacker.totEnergy>=atk.atkCost) { // only can go thru if the attacker has enough energy

            attacker.totEnergy -= atk.atkCost; //take away energy cost of attack from total energy

            System.out.println(attacker.name.toUpperCase() + " USES " + atk.atkName.toUpperCase() + "!");

            int actualDmgDealt = specialDmg(atk,atk.atkDmg);

            if(attacker.type.equalsIgnoreCase(victim.weak)){  // if victim has weakness against the attackers type, then victim takes double damage
                actualDmgDealt = 2*actualDmgDealt;
                victim.hp -= actualDmgDealt;
                if(actualDmgDealt>0) {
                    System.out.println(atk.atkName + " is super effective!");
                }
                else{
                    System.out.println(atk.atkName + " would've been super effective, but...");
                }

            }

            else if(attacker.type.equalsIgnoreCase(victim.resist)){  // if victim is resistant then do half dmg
                actualDmgDealt = actualDmgDealt/2;
                victim.hp -= actualDmgDealt;

                System.out.println(atk.atkName + " is super weak!");

            }

            else {  // if the victim is neither, then just do normal damage

                victim.hp -= actualDmgDealt;

            }

            System.out.println(atk.atkName.toUpperCase() + " DEALT " + actualDmgDealt + " DAMAGE"); // STATE DMG

            uiAfterFight(attacker,victim);

            specialEffect(attacker,victim,atk);

        }

        else{

            System.out.println("Not enough energy for " + atk); // not enough energy problem for enemy is dealt for so this wont show up for enemy turns ever

            uiFight();

            Scanner kb = new Scanner(System.in);
            int kbInt = kb.nextInt();

            chooseFight(kbInt,attacker,victim,evenNumber);// instead of letting them pick another attack, this lets them have the option of do something
            // in case they just realize they don't have enough energy for any attacks at all



        }



    }


    public static void uiAfterFight(Pokemon attacker,Pokemon victim){  // this method is for the user to clearly see the stats of
                                                                        // the pokemon on field
        if (victim.hp <= 0) {  // if the victim gets to 0 hp or below...

            System.out.println(victim.name.toUpperCase() + " DIED!\n");

            if(Arena.currentFighter.contains(victim)){  // IF VICTIM DIES AND WAS USERS POKE, REMOVE THEM

                Arena.currentFighter.remove(victim);

                if(Arena.playerPokes.isEmpty()){ // in case the player has no more pokemon, they lsoe

                    System.out.println("YOU LOSE !");

                    System.exit(0); // player lost so just exit..

                }


                Arena.pokeChoice(Arena.onFieldNum, Arena.playerPokes, Arena.currentFighter); // we need a new fighter from the user if we passed the above if statement

                uiFight();

                Scanner kb = new Scanner(System.in);
                int kbInt = kb.nextInt();

                chooseFight(kbInt,Arena.currentFighter.get(0),attacker,evenNumber); // now we r at the users turn with the new pokemon the chose!

            }

            else{ //if dead poke was from enemy then remove them from all the contender list

                Arena.allPokes.remove(victim);

                //disable effects should only last until the match is over/ when the current challenger has died
                disableUsedByPlayer = false;
                disableUsedByComp = false;

                chooseFight(0,attacker,Arena.randEnemyFinder(Arena.allPokes),oddNumber);

            }

        }

        System.out.println();

        System.out.println(attacker.name.toUpperCase() + " HP: " + attacker.hp);

        System.out.println(attacker.name.toUpperCase() + " ENERGY: " + attacker.totEnergy);


        System.out.println(victim.name.toUpperCase() + " HP: " + victim.hp);

        System.out.println(victim.name.toUpperCase() + " ENERGY: " + victim.totEnergy);
    }




    public static void uiFight(){
        callUI = false;
        System.out.println("\nCHOOSE AN ACTION: \n");
        System.out.println("1) ATTACK");
        System.out.println("2) RETREAT");
        System.out.println("3) PASS\n");
    }


    public static int specialDmg(Attack atk, int dmgInitial){ //special attack's damage

        String spcl = atk.atkSpcl;

        dmgDealt = dmgInitial;

//WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD //WINDCARD

        if(spcl.equalsIgnoreCase("wild card")){

            System.out.println("WILD CARD ACTIVATES");

            if(Arena.randint(0,1)==0){
                dmgDealt = dmgInitial*2;
                System.out.println("DAMAGE DOUBLES");
            }
            else{
                dmgDealt = 0;
                System.out.println("DAMAGE NEGATED");
            }
        }
//WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM //WILD STORM

        else if(spcl.equalsIgnoreCase("wild storm")){

            System.out.println("WILD STORM ACTIVATES");

            if(Arena.randint(0,1)==0){

                dmgDealt = dmgInitial*2;

                dmgDealt+=specialDmg(atk,dmgInitial); // if successful we want to redo the wild storm effect and add on any damage onto the current damage

            }
            else{

                dmgDealt = 0;

            }
        }
        return dmgDealt;
    }


    public static void specialEffect(Pokemon attacker, Pokemon victim, Attack atk){
        String spcl = atk.atkSpcl;

        if(spcl.equalsIgnoreCase("stun")){

//*********************************** PLAYER USES STUN ******************************************************//

            if(Arena.currentFighter.contains(attacker)){ // if the attacker with special of stun attacked,

                // the player's turn is basically over at that point so
                // just start the player turn again with taking care of everything that would happen if the
                // enemy were to just pass the turn.

                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                System.out.println(victim.name.toUpperCase() + " IS STUNNED");

                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                uiFight();

                Scanner kb = new Scanner(System.in);
                int kbInt = kb.nextInt();

                chooseFight(kbInt,attacker,victim,evenNumber); // even number meaning the ally turn starts
            }


//*********************************** COMPUTER USES STUN ******************************************************//
            else{

                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                System.out.println(victim.name.toUpperCase() + " IS STUNNED");

                callUI = false; // dont need the option to pick a move since the turn will be skipped anyways

                chooseFight(passMove,victim,attacker,evenNumber); // in the case the attacker is the computer then the player turn
                                                                    // will automatically be skipped/passed since 2 out of 3 options
            }                                                       // are gone..

        }

        if(spcl.equalsIgnoreCase("recharge")){


            System.out.println("RECHARGE ACTIVATES \n+ " + rechargeAmount+" ENERGY");
            attacker.totEnergy+=rechargeAmount;

        }

        if(spcl.equalsIgnoreCase("disable")){  // if the special is disable then..

            if(Arena.currentFighter.contains(victim)) { // if player's poke is disabled......

                if (!disableUsedByComp) { //if disable effect has not been used yet
                    for (int i = 0; i < victim.numAttacks; i++) { // then reduce all of the victims attacks to by the set reducer
                        if (victim.attacks[i].atkDmg >= disableReduceDmg) { //if their dmg is 10 or more,
                            victim.attacks[i].atkDmg -= disableReduceDmg;  //reduce the victims attack damage if its more than the reductions
                        } else { // if it is less than 10 then instead of negative damage, they should have 0 damage
                            victim.attacks[i].atkDmg = 0;
                        }
                    }

                    System.out.println("DISABLE LOWERS " + victim.name.toUpperCase() + "'S ATTACK DAMAGE");

                    disableUsedByComp = true; // and now it is true that the disable has been used

                    // NOTE THAT THIS AFFECT LASTS FOR THE WHOLE ROUND SO WE CAN RESET IF THE CURRENT ROUND IS OVER
                    // MEANING THE CURRENT OPPONENT HAS LESS THAN OR 0 HP
                }
            }

            else{ //if computer poke is disabled...............

                if (!disableUsedByPlayer) { //if disable effect has not been used yet
                    for (int i = 0; i < victim.numAttacks; i++) { // then reduce all of the victims attacks to by the set reducer
                        if (victim.attacks[i].atkDmg >= disableReduceDmg) { //if their dmg is 10 or more,
                            victim.attacks[i].atkDmg -= disableReduceDmg;  //reduce the victims attack damage if its more than the reductions
                        } else { // if it is less than 10 then instead of negative damage, they should have 0 damage
                            victim.attacks[i].atkDmg = 0;
                        }
                    }

                    System.out.println("DISABLE LOWERS " + victim.name.toUpperCase() + "'S ATTACK DAMAGE");

                    disableUsedByPlayer = true; // and now it is true that the disable has been used

                    // NOTE THAT THIS AFFECT LASTS FOR THE WHOLE ROUND SO WE CAN RESET IF THE CURRENT ROUND IS OVER
                    // MEANING THE CURRENT OPPONENT HAS LESS THAN OR 0 HP
                }
            }

        }


    }


    public static void chooseFight(int decision, Pokemon good, Pokemon bad, int turn){

        // at the end of each round, all pokes are meant to regen 10 energy, capped at 50. gaining energy at the start of round x+1
        // is the same as gaining energy at end of round x

        passiveEnergy(Arena.currentFighter);  //current fighter will gain 10 e

        passiveEnergy(Arena.playerPokes); //all pokes in players bench atm will also gain 10 e

        passiveEnergy(Arena.allPokes); //all enemy pokes gain 10 e

        while(bad.hp!=0) {                                  //////////////////WHILE CURRENT ENEMY NOT DEAD
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ USER TURN $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//
            if(turn%2==0){



                if(callUI) {// this is functional since it lets me take in user keyboard input before this point
                    uiFight();// I will basically display ui for moves BEFORE setting the chooseFight decision parameter and set callUI to false
                }
                callUI = true; // after the program gets to this line, it will set callUI back to true such that the UI displays next time we call chooseFight
                //===================================== ATTACK ==================================//

                if (decision == attackMove) {

                    System.out.println("CHOOSE AN ATTACK!\n");

                    for (int j = 1; j < good.numAttacks+1; j++) { //printing all pokemon names for player to see

                        System.out.println(j + ") " + good.attacks[j-1].atkName);

                    }

                    System.out.println();

                    Scanner kb = new Scanner(System.in);
                    int kbInt = kb.nextInt();

                    //for (int i = 0; i < good.numAttacks; i++) { //checking if attack is valid

                    if (kbInt<=good.numAttacks && 0<kbInt) {

                            dealDamage(good, bad, good.attacks[kbInt-1]);


                            turn++; // THE TURN IS OVER SO NOW WE MAKE IT ODD NUM FOR ENEMY
                    }

                    else{
                            System.out.println("PICK A VALID ATTACK");
                            chooseFight(attackMove,good,bad,turn);
                    }


                }

                //================================= RETREAT ======================================//

                else if (decision == retreatMove) {



                    System.out.println(Arena.currentFighter.get(0).name.toUpperCase() + " RETURN!"); // when pokemon returns to pokeball after fight. it heals to full

                    System.out.println("PICK A REPLACEMENT FOR "+ good.name.toUpperCase());

                    Arena.pokeChoice(1,Arena.playerPokes,Arena.currentFighter); // now we add a new fighter to the players fighting array list

                    Arena.playerPokes.add(good);

                    Arena.currentFighter.remove(good); // now we remove the pokemon we wanted to remove

                    good = Arena.currentFighter.get(0); // and this makes the replacing pokemon the pokemon we will be dealing with in this method

                    // turn basically over at this point. code below will only affect us next player turn (so after the round is over)

                    turn++;

                }


                //================================ PASS ======================================//


                else if (decision == passMove) {

                    System.out.println("TURN PASSED\n");

                    turn++;

                }


                //=============================== INVALID ========================================//

                else { // if the move picked was invalid...

                    System.out.println("CHOOSE A VALID MOVE");

                    // give them another chance

                    Scanner kb = new Scanner(System.in);
                    int kbInt = kb.nextInt();

                    chooseFight(kbInt,good,bad,turn);

                }

                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            }



//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ENEMY TURN $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//

            if(turn%2!=0){

                Attack enemyAtk = bad.attacks[Arena.randint(0,bad.numAttacks-1)];

                while(enemyAtk.atkCost>bad.totEnergy){ // just making sure the pokemon has enough enerygy to use the move, if not then keep picking rando attack till u get one

                    enemyAtk = bad.attacks[Arena.randint(0,bad.numAttacks-1)];

                    int checkIfAnyPossible = 0;  // num of possible attacks

                    for(int i = bad.attacks.length-1; i>-1; i--){   // check if there is any possible attack

                        if(bad.attacks[i].atkCost<=bad.totEnergy){

                            checkIfAnyPossible++;

                        }
                    }

                    if(checkIfAnyPossible==0){  // if no possible attacks then it can't attack so just go next turn

                        System.out.println(bad.name.toUpperCase() + " PASSES");

                        uiFight();

                        Scanner kb = new Scanner(System.in);
                        int kbInt = kb.nextInt();

                        chooseFight(kbInt,good,bad,turn+1);
                    }



                }

                dealDamage(bad,good,enemyAtk); //deal dmg with random attack

                uiFight();

                Scanner kb = new Scanner(System.in);
                int kbInt = kb.nextInt();

                chooseFight(kbInt,good,bad,turn+1);  //next turn


            }
        }

        disableUsedByComp = false;
        disableUsedByPlayer = false;
        // the current battle ends at this point meaning that if a pokemon's damage was reduced, then we want
        //to undo it

    }

    public static void passiveEnergy(ArrayList<Pokemon> pokemons){  // makes every pokemon in given array list gain 10 energy capped at 50

        for(int i = 0; i < pokemons.size(); i++){  // for every pokemon in the array list
                pokemons.get(i).totEnergy += Arena.addedEnergy;    // add 10 energy
                if(pokemons.get(i).totEnergy > Arena.intialEnergy){    // if the energy is above 50 now
                    pokemons.get(i).totEnergy = Arena.intialEnergy; // make it 50
                }

        }

    }
    public String toString(){
        return name + "\n";
    } //overriding the toString() built in method

    public String getName(){
        return name;
    }

    public int getFullHp(){
        return fullHp;
    }

    public void setHp(int newHp){
        this.hp = newHp;
    }

    public void setTotEnergy(int newEnergy){
        this.totEnergy = newEnergy;
    }

}



//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//



class Attack{

    String atkName; //name of attack

    int atkCost; //cost of attack (in energy)

    public int atkDmg; //damage of attack

    String atkSpcl; //special effect of attack

    public Attack(String atkN,  int atkD, int atkC, String atkS){ //name, energy cost, damage, and special, respectively // attack object constructor
        atkName = atkN;
        atkCost = atkC;
        atkDmg = atkD;
        atkSpcl = atkS;
    }
    @Override
    public String toString(){
        return atkName + "\n";
    }

}