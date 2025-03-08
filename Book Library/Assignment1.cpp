// ASU CSE310 Spring 2025 Assignment #1
// Name:Ruby Gonzalez Alvarado
// ASU ID:1223659710
// Description: This program displays a menu of choices to a user
//              and performs the chosen task. It will keep asking a user to
//              enter the next choice until the choice of 'Q' (Quit) is entered.
//---- is where you should add your own code

//add your own code
//----

#include <iostream>
#include "LinkedList.h" //reference our other doc of code 

using namespace std;

void printMenu();

int main()
{
	// local variables
	char input1 = 'Z';
	string inputInfo;
	string title, edition, newTitle;
	int pages, num;
	double price;
	bool success = false;

	// instantiate a Linked List object
	LinkedList* list1 = new LinkedList();

	printMenu();

	do  //ask for user input
	{
		cout << "\nWhat action would you like to perform?\n";
		cin.get(input1);
		input1 = toupper(input1);    //change it to upper case
		cin.ignore(20, '\n'); //flush the buffer

		// matches one of the following cases
		switch (input1)
		{
		case 'A':   //Add the Book
			cout << "\nEnter the book information: ";
			cout << "\nEnter book title: ";
			
			getline(cin, title); //getting title from user

			cout << "\nEnter book edition: ";
			
			getline(cin, edition); //get edition from user
			
			cout << "\nEnter book pages: ";
			
			cin >> pages; //get page number from user
			
			cout << "\nEnter book price: ";
			cin >> price; //get book price
			cin.ignore(20, '\n'); //flush the buffer

			

			success = list1 -> addBook(title, edition, pages, price); //want to check if we can add book no dublicates found or we print error mssg

			
			if (success == true) //no duplicate found then we procceed to tell user node (book) is added to list
				cout << "\nBook: \"" << title << "\", " << edition << " edition is added\n";
			else
				cout << "\nBook: \"" << title << "\", " << edition << " edition is NOT added\n";
			break;

		case 'C':  //Change a Book title
			cout << "\nEnter the original book title which you want to change: ";
			
			getline(cin, title); //getting title of book we want to change

			cout << "\nEnter the book edition: ";
			
			getline(cin,edition); //getting specific edition of book
			
			success = list1->findBook(title, edition); // if we find book in out list proceed

			//enter new the CHANGED (new)title 
			if(success){
			cout << "\nEnter the new book title:"; 
			getline(cin, newTitle);
			}
			success = list1->changeBookTitle(title, edition, newTitle);
			
			//we sucessfull change title we print mmsg letting user know new name
			if (success) {
				cout << "\nBook: \"" << title << "\", " << edition << " edition is changed to: " << newTitle << "\n";
			}
			else {
				cout << "\nBook: \"" << title <<"\", " << edition << " edition does not exist\n";
			}
			
			break;

		case 'D':   //Display all books
			
			list1->printBookList();

			break;

		case 'E':   //Display books by title
			cout << "\nEnter the book title you want to display: ";
			
			getline(cin, title);
			
			cout << "\n";
			
			list1->printBookListByTitle(title); //look through list and chose specific books w/ titled matched
			

			break;

		case 'F':   //Find a Book
			cout << "\nPlease enter the book title you want to find: ";
			
			getline(cin,title); //get line for title
			
			cout << "\nPlease enter the book edition: ";
			
			getline(cin,edition); //get line for edition
			

			success = list1-> findBook(title,edition);  // look through our list to match book title/edition, proceed w/ message

			if(success) {
				cout << "\nBook: \"" << title << "\", " << edition << " edition is Found\n";
			} else {
				cout << "\nBook: \"" << title << "\", " << edition << " edition is NOT Found\n";
			}

			break;

		case 'U':  //Update a Book price & get title/edition
			cout << "\nEnter the book title which you want to change the price: ";
			
			getline(cin,title);
			
			cout << "\nEnter the book edition: ";
			
			getline(cin,edition);
			

			
      success = list1 -> findBook(title, edition);

		  if(success) {
				cout << "\nPlease enter the new price: "; //after and when we find book ask user for new price
				cin >> price;
				cin.ignore(20, '\n'); 
         
		 success = list1->updatePrice(title, edition, price); // update price on list if success, and print message
          
          if(success){
				cout << "\nBook: \"" << title << "\"," << edition << " edition, its price was updated\n";
				} 
			   
			}else {
				cout << "\nBook: \"" << title << "\"," << edition << " edition, does NOT exist\n"; //no change price/no book found
}
			
			break;

		case 'R':  //Remove a specific Book
			cout << "\nEnter the book title you want to remove: ";
			
			getline(cin, title);
			
			cout << "\nEnter the book edition: ";
			
			getline(cin, edition);
			

			success=list1->removeBook(title, edition); //true if book is found we remove (print appropriate mssg)

			if(success) {
				cout << "\nBook: \""  << title << "\"," << edition << " edition was removed\n";
			} else {
				cout << "\nBook: \""  << title << "\","  << edition << " edition does NOT exist\n";

			}

			break;

		case 'N':  //Remove books by edition
			cout << "\nEnter the book edition which you want to remove: ";
			
			getline(cin, edition);

			num = list1->removeByEdition(edition); //looking for number of editions that match to remove from list
			
			if (num>0)
				cout << "\n" << num << " books with " << edition << " edition were removed\n";
			else
				cout << "\nBooks with " << edition << " edition does NOT exist\n";

			break;

		case 'T':  //Remove books by Title
			cout << "\nEnter the book title which you want to remove: ";
			//----
			getline(cin, title);


			num = list1->removeByTitle(title); // look for matching titles but we use counter to return w/ our message
			if (num > 0)
				cout << "\n" << num << " books with title: " << title << " were removed\n";
			else
				cout << "\nBooks with title: " << title << " does NOT exist\n";
			break;

		case 'Q':   //Quit
			delete list1;
			break;

		case '?':   //Display Menu
			printMenu();
			break;

		default:
			cout << "Unknown action\n";
			break;
		}
	} while (input1 != 'Q');
	return 0;
} //end main()

/** The method printMenu displays the menu to a user**/
void printMenu()
{
	cout << "Choice\t\tAction\n";
	cout << "------\t\t------\n";
	cout << "A\t\tAdd a Book\n";
	cout << "C\t\tChange a Book Title\n";
	cout << "D\t\tDisplay All Books\n";
	cout << "E\t\tDisplay Books by Title\n";
	cout << "F\t\tFind a Book\n";
	cout << "U\t\tUpdate a Book Price\n";
	cout << "R\t\tRemove a Book\n";
	cout << "N\t\tRemove Books by Edition\n";
	cout << "T\t\tRemove Books by Title\n";
	cout << "Q\t\tQuit\n";
	cout << "?\t\tDisplay Help\n\n";
}