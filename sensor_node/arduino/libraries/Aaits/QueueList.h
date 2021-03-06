/*
 *  QueueList.h
 *
 *  Library implementing a generic, dynamic queue (linked list version).
 *
 *  ---
 *
 *  Copyright (C) 2010  Efstathios Chatzikyriakidis (contact@efxa.org)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  ---
 *
 *  Version 1.0
 *
 *    2010-09-28  Efstathios Chatzikyriakidis  <contact@efxa.org>
 *
 *      - added exit(), blink(): error reporting and handling methods.
 *
 *    2010-09-25  Alexander Brevig  <alexanderbrevig@gmail.com>
 *
 *      - added setPrinter(): indirectly reference a Serial object.
 *
 *    2010-09-20  Efstathios Chatzikyriakidis  <contact@efxa.org>
 *
 *      - initial release of the library.
 *
 *  ---
 *
 *  For the latest version see: http://www.arduino.cc/
 */

// header defining the interface of the source.
#ifndef _QUEUELIST_H
#define _QUEUELIST_H
#include "Arduino.h"


// the definition of the queue class.
template<typename T>
class QueueList {
  public:
    // init the queue (constructor).
    QueueList ();

    // clear the queue (destructor).
    ~QueueList ();

    // push an item to the queue.
    void push (const T i);

    // pop an item from the queue.
    T pop ();

    // get an item from the queue.
    T peek () const;
    
    // get last inserted item
    T peekLast ();

    // check if the queue is empty.
    bool isEmpty () const;

    // get the number of items in the queue.
    int count () const;
    
    //
    void goToFirst();
    
    // get the next item of the list
    T getNext() const;
    
    //remove current node
    void removeItem( T item);
    
    //remove all nodes
    void removeAll();

  private:
    // exit report method in case of error.
    void exit (const char * m) const;

    // the structure of each node in the list.
    typedef struct node {
      T item;      // the item in the node.
      node * next; // the next node in the list.
    } node;

    typedef node * link; // synonym for pointer to a node.

    int size;        // the size of the queue.
    link head;       // the head of the list.
    mutable link cursor;    // cursor to iterate through the queue
    link tail;       // the tail of the list.

};

// init the queue (constructor).
template<typename T>
QueueList<T>::QueueList () {
  size = 0;       // set the size of queue to zero.
  head = NULL;    // set the head of the list to point nowhere.
  tail = NULL;    // set the tail of the list to point nowhere.
  cursor = NULL;  // set the cursor of the list to point nowhere.
}

// clear the queue (destructor).
template<typename T>
QueueList<T>::~QueueList () {
    removeAll();
}

// clear the queue (destructor).
template<typename T>
void QueueList<T>::removeAll () {
  // deallocate memory space of each node in the list.
  for (link t = head; t != NULL; head = t) {
    t = head->next; delete head;
  }

  size = 0;       // set the size of queue to zero.
  head = NULL;
  tail = NULL;    // set the tail of the list to point nowhere.
  cursor = NULL;
}

//get the last inserted item
template<typename T>
T QueueList<T>::peekLast() {
    return (tail->item);
}

// push an item to the queue.
template<typename T>
void QueueList<T>::push (const T i) {
  // create a temporary pointer to tail.
  link t = tail;

  // create a new node for the tail.
  tail = (link) new node;

  // if there is a memory allocation error.
  if (tail == NULL) ;

  // set the next of the new node.
  tail->next = NULL;

  // store the item to the new node.
  tail->item = i;

  // check if the queue is empty.
  if (isEmpty ())
    // make the new node the head of the list.
    head = tail;
  else
    // make the new node the tail of the list.
    t->next = tail;
  
  // increase the items.
  size++;
}

// pop an item from the queue.
template<typename T>
T QueueList<T>::pop () {
  // check if the queue is empty.
  if (isEmpty ()) ;

  // get the item of the head node.
  T item = head->item;

  // remove only the head node.
  link t = head->next; delete head; head = t;

  // decrease the items.
  size--;

  // return the item.
  return item;
}

// get an item from the queue.
template<typename T>
T QueueList<T>::peek () const {
  // check if the queue is empty.
  if (isEmpty ()) ;

  // return the item of the head node.
  return head->item;
}

//Returns the next item (use goToFirst()) to reset position
template<typename T>
T QueueList<T>::getNext() const {
    link cur_node;
    cur_node = cursor;
    
    if(cur_node!= NULL) {
        cursor = (link) cur_node->next;
        return cur_node->item;
    }

    return NULL;
}

//sets the cursor to to first item
template<typename T>
void QueueList<T>::goToFirst() {
    cursor = head;
}

/** @brief removes first occurence of item in queue */
template<typename T>
void QueueList<T>::removeItem( T item) {

    link aux;

    if(item == NULL) return;
    
    //Delete 1st elemment
    if(item == head->item) {
        aux = head->next;
        delete head;
        head = aux;
        size--;
        return;
    }
    

    aux = head;
    do {
        if(aux->next != NULL && aux->next->item == item) {
            if(aux->next == tail) {
                //last item
                aux->next = NULL;
                delete tail;
                tail = aux;
            } else {
                //middle item
                link aux2 = aux->next;
                aux->next = aux->next->next;                
                delete aux2;
            }
            
            size--;
            return;
        }
    } while( aux = aux->next);

}


// check if the queue is empty.
template<typename T>
bool QueueList<T>::isEmpty () const {
  return head == NULL;
}

// get the number of items in the queue.
template<typename T>
int QueueList<T>::count () const {
  return size;
}


#endif // _QUEUELIST_H
