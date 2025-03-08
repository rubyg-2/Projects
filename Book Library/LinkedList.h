// ASU CSE310 Spring 2025 Assignment #1
// Name: Ruby Gonzalez Alvarado
// ASU ID:1223659710
// Description: This programs has the functions that users will interact with when displayed a menu. 
// functions are connected to a link list and will be analyzed according to user's choice which 
// include removing by title/edition, adding book, changing price/title, finding specific book, and printing current list

#include <iostream>
#include <iomanip>
#include <string>

using namespace std;

//this represents a Book object
struct Book
{
	string title, edition;
	int pages;
	double price;
	struct Book* next;
};

//class LinkedList will contains a linked list of Books
class LinkedList
{
private:
	struct Book* head;

public:
	LinkedList();
	~LinkedList();
	bool findBook(string title, string edition);
	bool addBook(string title, string edition, int pages, double price);
	bool removeBook(string title, string edition);
	int removeByEdition(string edition);
	int removeByTitle(string title);
	bool changeBookTitle(string oldTitle, string edition, string newTitle);
	bool updatePrice(string title, string edition, double newPrice);
	void printBookListByTitle(string title);
	void printBookList();
};

//Constructor to initialize an empty linked list
LinkedList::LinkedList()
{

	head = nullptr;


}

//Destructor
//Before termination, the destructor deletes all the nodes and prints the number of nodes deleted by it.
LinkedList::~LinkedList()
{


	int bookCount = 0; //book counter 
	Book* temp = head; //currently pointing at the head
	while(temp != nullptr) {
		Book* toDelete = temp;
		temp = temp->next;
		delete toDelete;
		bookCount++;

	}
	//as long as current isn't empty deletion of specific book will occur

	cout<< "\nThe number of deleted Book is: " << bookCount <<"\n";
}

//A function to identify if the parameterized Book is inside the LinkedList or not.
//Return true if it exists and false otherwise.
bool LinkedList::findBook(string title, string edition)
{
	

	Book* temp = head; //at head
	while (temp != nullptr) { //not empty
		if(temp->title == title && temp->edition == edition) { //matching book by title AND edition
			return true;
		}
		temp = temp ->next; //node continues

	}

	return false;


}

//Creates a new node and inserts it into the list at the right place.
//It also maintains an alphabetical ordering of books by their title and edition, i.e.
//first by title, if two books have the same title, then we will list them in alphabetical order
//of the edition.
//Return value: true if it is successfully inserted and false otherwise
bool LinkedList::addBook(string title, string edition, int pages, double price)
{
	//if the Book already exist, print the message and return false
	if (findBook(title,edition)) // 
	{
		cout << "\nDuplicated Book. Not added.\n";
		return false;
	}

	Book* newBook = new Book{title, edition, pages, price, nullptr}; //gaining info on new book (newnode)

	if(head == nullptr || title < head->title || (title == head->title && edition < head->edition)) { //check book to see where it will inset
		newBook -> next = head;
		head = newBook; //head points to new book
		return true; //has been added
	} 

	Book* temp = head;
	while (temp->next != nullptr && (temp ->next-> title < title || (temp->next->title == title && temp->next->edition < edition))) {
		temp = temp -> next;
	} //check if title matches if so put in order and checks next if title is same followed by ordering in alphabetical based edition
	
	//insert book according to order
	newBook -> next = temp->next;
	temp->next = newBook;
	return true; //done
}




//Return true if it is successfully removed, false otherwise. Note: only book with
//the same title AND edition should be removed from the list.
bool LinkedList::removeBook(string title, string edition)

{
	if(head == nullptr) {
		return false; //nothing to remove
	}

	if (head -> title == title && head -> edition == edition) {  //look for matchich title /edition
		Book* toDelete = head; //save to delete  at head
		head = head -> next; //update where we point next 
		delete toDelete; //delete 
		return true; //success
	}
	
	Book* temp = head;
	while (temp -> next != nullptr){
	    if(temp -> next -> title == title && temp -> next -> edition == edition ){ //find match
	        Book* toDelete = temp -> next; //save node to delete
	        temp -> next = temp -> next -> next; //uddate pointer to look over node we are deleting
	        delete toDelete;
	        return true; //succes
	}
		temp = temp -> next;

	}

return false;//no book found
    
}

//Removes all books with the specified edition from the list.
//Return number of books which are successfully removed.
int LinkedList::removeByEdition(string edition)
{

	int bookCount = 0; 
	Book* temp = head;
	Book* prev = nullptr;

	while( temp != nullptr) { 
		if(temp -> edition ==edition) { //match edition for removal
			Book* toDelete = temp;
			if(prev == nullptr) { //head matchd
				head = temp->next;
			} else {
				prev->next = temp->next; //update 
			}
			temp = temp -> next; //next node
			delete toDelete;
			bookCount++; //update counter after deletion
		} else {
			prev = temp; // doesnt match continue to next
			temp = temp -> next;
		}
	}

	return bookCount; //return #  successful removal

}

//Removes all books with the specified title from the list.
//Return number of books which are successfully removed.
int LinkedList::removeByTitle(string title)
{
		
	int bookCount = 0; //track num deletion
	Book* temp = head; //start at head
	Book* prev = nullptr;

	while (temp != nullptr) { 
		if(temp -> title == title) {//find match by title
			Book* toDelete = temp;
			if(prev == nullptr) {
				head= temp -> next; //update if found at start
			} else {
				prev-> next = temp -> next;
			}
			temp = temp -> next; //keep moving to next node 
			delete toDelete;
			bookCount++; //increase when deletions are complete
		} else {
			prev = temp; //update
			temp = temp -> next; //move to next node
		}
	}

	return bookCount; //return number of deltion to user

}

//Modifies the title of the given Book. Return true if it modifies successfully and
//false otherwise. Note: after changing a Book title and edition, the linked list must still
//maintain the alphabetical order.
bool LinkedList::changeBookTitle(string oldTitle, string edition, string newTitle)
{
	
	Book* temp = head;
	while(temp!=nullptr){
   		if(temp->title == oldTitle && temp-> edition == edition){ //find match title/edition
        	int pages = temp -> pages; //keep our ouriginal page count
        	double price = temp -> price; // keep our orignal price
        
       		removeBook(oldTitle,edition); //remove book
        
       		return addBook(newTitle,edition,pages,price); //return new info w/ update 
    }
    	temp = temp -> next; //move to next node
}
return false;


}


//Update the specific book's price. Note: a book is uniquely identified by its title and edition
bool LinkedList::updatePrice(string title, string edition, double newPrice)
{
	Book* temp = head;
	while(temp != nullptr) {
		if(temp -> title == title && temp -> edition == edition) {  //match title/edition
			temp -> price = newPrice; // update price
			return true; //dome
		}
		temp = temp -> next; //no match look to next node
	}
    return false;

}

//Prints all Books in the list with the same title.
void LinkedList::printBookListByTitle(string title)
{

    Book* temp = head;
    bool foundBook = false;
    while(temp !=nullptr){
        if(temp->title == title){
            foundBook = true; //found matching book
    
	//alignment
        cout << left << setw(35) << temp->title
         << left    << setw(5) << temp->edition
         << right   << setw(8) << temp->pages
         << setw(8) << fixed << setprecision(2) << temp->price << "\n";
        }
        temp = temp -> next; //move next node
    }
    if(!foundBook ){
        cout <<"\nNo books with the specified title found.\n";
    }	

}

//Prints all books in the linked list starting from the head.
void LinkedList::printBookList()
{
   //no print = empty
    if(head == nullptr){
        cout <<"\nThe list is empty\n";
        return;
    }
    
	//print list
    Book* temp = head;
    while(temp != nullptr){
    
    	cout << left << setw(35) << temp->title
	     << left    << setw(5) << temp->edition
	     << right   << setw(8) << temp->pages
	     << setw(8) << fixed << setprecision(2) << temp->price << "\n";
	     
	     temp = temp -> next; //move to next node
    }
	
}