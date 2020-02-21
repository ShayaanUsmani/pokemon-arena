import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Arena {
    //----------------------STATIC CITY----------------------------------------//
    static int turn = randint(0,1); //starts at an even or odd num which will be set as the players turn if turn module 2 is 0 and comp otherwise
    static int startingPokesNumP = 4; //playerstarts with this many pokemon
    static int onFieldNum = 1; //num of pokes on field
    static int numOfPokes; //num of total pokemons

    static int intialEnergy = 50; // every pokemon starts with this much energy
    static int addedEnergy = 10; // amount of energy we want to add every round


    public static ArrayList<Pokemon> allPokes = new ArrayList<Pokemon>(); //arraylist containing all pokemons
    public static ArrayList<Pokemon> playerPokes = new ArrayList<Pokemon>(); //holds players pokemons
    public static ArrayList<Pokemon> currentFighter = new ArrayList<Pokemon>(); //holds fighting pokemons (ally) NOTE this is an arrlist instead of 1 object since it can be used in
                                                                                //methods an object cant be used in



    //------------------------------------------------------------------------//
    public static void main(String[] args)throws IOException{ //there sus a chance the user won't have the necessary file format or file needed to run properly
            try {

                Scanner stats = new Scanner(new BufferedReader(new FileReader("pokemon.txt"))); //#############################################################################################################################################################

                numOfPokes = stats.nextInt();//first int of the pokemon.txt file is the num of pokemons

                stats.nextLine(); //first line in file read is the total number of pokemons so we want to skip that


                while(stats.hasNextLine()) {

                    String line = stats.nextLine(); //we will use up this line to create a pokemon object


                    allPokes.add(new Pokemon(line)); // for all the lines about profiles of pokemons, make a pokemon object using info in the lines from file
                                                        // and put all of these pokemon objects into the allPokes array list



                }

//===========================================================CHOOSING INITIAL POKEMON===================================================================================================//

                pokeChoice(startingPokesNumP, allPokes, playerPokes); //num of pokes player is allowed at start

//=========================================================================================================================================================//

//====================================================== FIGHTING  ===================================================================================================//
                fight();
            }
            catch(IOException err){
                System.out.println(err);
            }
    }
                    //---------------------------- CHOoSING POKEMON FOR USER ----------------------------------//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
                    public static void pokeChoice(int choices, ArrayList<Pokemon> availablePokes, ArrayList<Pokemon> chosenPokes) // availablePokes = arraylist containing all the available pokemon to pick from
                    {                                                                                                               // chosenPokes = the arraylist which the chosen pokemon will reside in
                                                                                                                                    // choices = how many pokemon can be chosen from all the available

                        //        display of available pokes          //
                        System.out.println("==========");

                        for (int i = 0; i < availablePokes.size(); i++)
                        {

                            System.out.println(i + 1 + ") " + availablePokes.get(i).getName()); //since the names are private and protected from this class, we must use the getter method

                        }
                        System.out.println("==========");
                        ////////////////////////////////////////////////////////////////////////////////



                        //           text advising to pick pokemon               //
                        if (choices == 1)
                        {
                            System.out.println("Pick a Pokemon by its number from above!:\n");
                        }
                        else
                        {
                            System.out.println("Pick " + choices + " Pokemons by numbers from above!:\n");
                        }
                        /////////////////////////////////////////////////////////////////////////////

                        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

                        for (int chosenSoFar = 0; chosenSoFar < choices; chosenSoFar++)
                        { //how many pokemons they have chose so far represented by chosenSoFar

                            //              title of pokemon being chosen            //

                            if (choices == 1)
                            {
                                System.out.println("Pokemon:\n");
                            }
                                                                        // if the user is only picking one poke, we shouldn't say Pokemon 1:
                            else if (choices > 1)
                            {
                                System.out.println("Pokemon " + (chosenSoFar + 1) + ":"); // chosenSoFar starts at 0 and it would be weird to as for Pokemon 0:
                            }

                            /////////////////////////////////////////////////////////////////////////

                            Scanner kb = new Scanner(System.in);
                            int kbInt = kb.nextInt(); //kbInt = keyboard integer


                            if ((kbInt <= availablePokes.size()) && (kbInt > 0) && (!chosenPokes.contains(availablePokes.get(kbInt - 1))))
                            { // if the number in is less than or equal to size of arr list and greater than 0,
                                // and if the pokemon object doesn't exist in the main array list then go ahead
                                chosenPokes.add(availablePokes.get(kbInt - 1));    //add the desired pokemon to the array list we want to add to


                            }

                            else
                            { // if the input wasn't valid then...
                                System.out.println("PICK A VALID POKEMON"); //just let player know they gotta pick a new one
                                chosenSoFar--; //and counteract the iteration of the for loop using "chose" so the player can try to pick a valid pokemon without having to start with less than desired num
                            }

                            //////////////////////////////////////////////////////////////





                        }
                        for (int i = 0; i < chosenPokes.size(); i++)
                        { // remove all the chosen pokemon from the original array list

                            availablePokes.remove(chosenPokes.get(i));

                        }
                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                    }




    public static Pokemon randEnemyFinder(ArrayList<Pokemon> arrList){

        Pokemon enemy = arrList.get(randint(0, arrList.size() - 1)); // want a random enemy pokemon on field

        System.out.println("*********************************");

        System.out.printf("A WILD %s APPEARS \n", enemy.getName().toUpperCase()); // print to announce the pokemon

        return enemy;

    }


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//




    public static void fight(){


        int nullPokeCounter = 0; //num of null pokemon in main enemy array list

        for(int i = 0; i < allPokes.size();i++){
            if(allPokes.get(i) == null){
                nullPokeCounter++;
            }
        }

        if(nullPokeCounter == allPokes.size()){ // if theres no pokemons for the user to verse then end the game and tell them they win

            System.out.println("CONGRATULATIONS! YOU ARE THE TRAINER SUPREME!");

            System.exit(0);

        }


        Pokemon onFieldE = randEnemyFinder(allPokes);




        //---------------------/CHOSING THE PLAYER FIGHTING POKEMON/------------------------------//

            if(!currentFighter.isEmpty()){ // if the turn ended with user killing enemy, return pokemon to their bench refreshed

                playerPokes.add(currentFighter.get(0));

                currentFighter.get(0).setHp(currentFighter.get(0).getFullHp());

                currentFighter.get(0).setTotEnergy(intialEnergy);

                System.out.println(currentFighter.get(0).getName() + " RETURNS!"); // when pokemon returns to pokeball after fight. it heals to full

                currentFighter.remove(0);

            }

            pokeChoice(onFieldNum,playerPokes, currentFighter); // we want 1 pokemon from players pokemons to fight //UPDATES CURRENT FIGHTER TO CHOSEN POKEMON, NO NEED FOR VAR

            Pokemon onFieldP = currentFighter.get(0); //on field PLAYER

            System.out.printf("\n%s, I CHOOSE YOU!\n\n", onFieldP.getName().toUpperCase());

//-----------------------------------------------TURNS___________________________________________________BEGIN------------------------------------------------------------------------------//


//---------------------/PLAYER TURN/------------------------------//

            if(turn%2==0) {

                Pokemon.callUI = false;
                Pokemon.uiFight();

                        // SCANNER //
                Scanner kb = new Scanner(System.in);
                int kbInt = kb.nextInt();


                Pokemon.chooseFight(kbInt, onFieldP, onFieldE, turn); //choose wat user does during turn with their selected pokemon

            }
//
////---------------------/ENEMY TURN/------------------------------//
            else{

                System.out.println(onFieldE.getName().toUpperCase() + " INITIATES THE BATTLE!\n");

                int choiceNotMatter = 0; //choice doesn't matter so any int, even 1 2 3 would work

                Pokemon.chooseFight(choiceNotMatter, onFieldP, onFieldE, turn);  // because turn will be odd / the enemy's anyways

            }



    }
    public static int randint(int low, int high){
        return (int)(Math.random()*(high-low+1)+low);
    } //randomly generates numbers given the lowest number you want generated, and the highest number
}