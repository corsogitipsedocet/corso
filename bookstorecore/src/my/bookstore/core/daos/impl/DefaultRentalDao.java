package my.bookstore.core.daos.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.bookstore.core.daos.RentalDao;
import my.bookstore.core.model.BookModel;
import my.bookstore.core.model.RentalModel;

public class DefaultRentalDao extends AbstractItemDao implements RentalDao {

	@Override
	public List<RentalModel> getActiveRentalsForCustomer(final CustomerModel customer) {
		// This could be done using GenericDao but for learning purposes we are
		// using Flexiblesearch
		// TODO exercise 5.2
		// TODO exercise 5.3
	    // add your implementation below

		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final Date dayStart = cal.getTime();
		cal.add(Calendar.DATE, 1);
		final Date dayEnd = cal.getTime();

		/*
		 * This is what the select statement will look like once assembled.
		 * final String queryString = "SELECT {rental.pk}" +
		 * " FROM {Rental as rental}" + " WHERE {rental.startDate} <= ?tomorrow"
		 * + " and {rental.endDate} >= ?today" +
		 * " and {rental.customer} = ?customer";
		 */

		final String queryString = "SELECT {rental.pk}" + " FROM {" + RentalModel._TYPECODE + " as rental}"
				+ " WHERE {rental." + RentalModel.STARTDATE + "} <= ?tomorrow" + " and {rental." + RentalModel.ENDDATE
				+ "} >= ?today" + " and {rental." + RentalModel.CUSTOMER + "} = ?customer";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("tomorrow", dayEnd);
		query.addQueryParameter("today", dayStart);
		query.addQueryParameter("customer", customer.getPk());
		return getFlexibleSearchService().<RentalModel> search(query).getResult();
	}

	@Override
	public List<BookModel> getMostRentedBooks(final int numberOfBooks) {
		// TODO exercise 5.4
		// TODO exercise 5.5
		/*
		 * This is what the select statement will look like once assembled.
		 * final String queryString = "SELECT pk" +
		 * " FROM ({{SELECT COUNT(*) as num, {Book.pk} as pk" +
		 * " FROM {Rental JOIN Book ON {Rental.product} = {Book.pk}}" +
		 * " GROUP BY {Rental.product}, {Book.pk}" +
		 * " ORDER BY num DESC LIMIT ?limit}})";
		 */

		final String queryString = "SELECT pk" + " FROM ({{SELECT COUNT(*) AS num, {Book." + BookModel.PK + "} AS pk"
				+ " FROM {" + RentalModel._TYPECODE + " JOIN " + BookModel._TYPECODE + " ON {Rental."
				+ RentalModel.PRODUCT + "}={Book." + BookModel.PK + "}}" + " GROUP BY {Rental." + RentalModel.PRODUCT
				+ "}, {Book." + BookModel.PK + "}" + " ORDER BY num DESC LIMIT ?limit}})";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("limit", numberOfBooks);
		final SearchResult<BookModel> books = getFlexibleSearchService().search(query);
		return books.getResult();
	}

}
