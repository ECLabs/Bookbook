package bookbook.utils;

import org.neo4j.graphdb.Direction
import org.neo4j.helpers.collection.PositionedIterator
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import bookbook.domain.Book
import bookbook.domain.User;
import bookbook.domain.Opinion;

import bookbook.domain.Opinion;
import bookbook.domain.CheckIn;
import bookbook.services.BookService
import bookbook.services.CheckinService
import bookbook.services.OpinionService
import bookbook.services.UserService

public class ActivityStreamIterator implements Iterator, Iterable{
   //private theobjects
   def userService
   def bookService
   def opinionService
   def checkinService
   ArrayList<PositionedIterator<ActivityIF>> iteratorList = new ArrayList<PositionedIterator<ActivityIF>>();
   private StatusUpdateComparator comparator = new StatusUpdateComparator();

   public ActivityStreamIterator(User user, UserService userService, OpinionService opinionService, CheckinService checkinService){
	   this.userService = userService
	   this.opinionService = opinionService
	   this.checkinService = checkinService

	  def friendsAndMe = userService.findFollowList(user.userId, Direction.OUTGOING)
	  friendsAndMe.add(user)
	  for ( User friend : friendsAndMe )
	  {
		  log.trace "in activity stream look, getting iterators for " + friend.userId
		  Iterator iterator = opinionService.getUserOpinions(friend).iterator();
		  if (iterator && iterator.hasNext()) {
			  iteratorList.add(new PositionedIterator<ActivityIF>(iterator));
		  }
		  
		  log.trace "in activity stream, getting iterators for checkins for " + friend.userId
		  Iterator iterator2 = checkinService.getUserCheckins(friend).iterator();
		  if (iterator2 && iterator2.hasNext()) {
			  iteratorList.add(new PositionedIterator<ActivityIF>(iterator2));
		  }
		  
	  }

	  sort();
	  
   }
   
   public ActivityStreamIterator(Book book, BookService bookService, OpinionService opinionService, CheckinService checkinService){
	   this.userService = userService
	   this.opinionService = opinionService
	   this.checkinService = checkinService
	  
	  log.trace "in activity stream, getting iterators for opinions for " + book?.bookId
	  Iterator iterator = opinionService.getBookOpinions(book).iterator();
	  if (iterator && iterator.hasNext()) {
		  iteratorList.add(new PositionedIterator<ActivityIF>(iterator));
	  }
	  
	  log.trace "in activity stream, getting iterators for checkins for " + book.bookId
	  Iterator iterator2 = checkinService.getBookCheckins(book).iterator();
	  if (iterator2 && iterator2.hasNext()) {
		  iteratorList.add(new PositionedIterator<ActivityIF>(iterator2));
	  }

	  sort();
	  
   }

   boolean hasNext(){
	  return iteratorList.size() > 0
   }
   
   Object next(){
	  if ( iteratorList.size() == 0 )
	   {
		   throw new NoSuchElementException();
	   }
	   // START SNIPPET: getActivityStream
	   PositionedIterator<ActivityIF> first = iteratorList.get(0);
	   Object returnVal = first.current();

	   if ( !first.hasNext() )
	   {
		   iteratorList.remove( 0 );
	   }
	   else
	   {
		   first.next();
		   sort();
		   log.trace "**** SORTING... ****"
	   }

	   return returnVal;
	   // END SNIPPET: getActivityStream
   }

   Iterator iterator(){
	  return this;
   }


   void remove(){
	  throw new UnsupportedOperationException("remove() not supported")
   }
   
   private void sort()
   {
	   Collections.sort( iteratorList, comparator );
   }
   private class StatusUpdateComparator implements Comparator<PositionedIterator<ActivityIF>> {
	   public int compare(PositionedIterator<ActivityIF> a, PositionedIterator<ActivityIF> b) {
   		def aIs = a instanceof CheckIn ? "checkIn" : "opinion"
   		def bIs = b instanceof Opinion ? "opinion" : "checkIn"
   		log.trace("Comparing ${aIs} iterator " + new Date(a.current().getCreateDate()) + "to ${bIs} iterator" + new Date(b.current().getCreateDate()))
   		log.trace("Result" + new Date(b.current().getCreateDate()).compareTo(new Date(a.current().getCreateDate())))
		return new Date(b.current().getCreateDate()).compareTo(new Date(a.current().getCreateDate()));
	   }
   }
   

}