package fr.epsi.book.dal;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;
import fr.epsi.book.domain.Contact.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BookDAO implements IDAO<Book, Long> {

	private static final String INSERT_QUERY = "INSERT INTO book (id, code) values (?,?)";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM book WHERE id=?";
	private static final String FIND_ALL_QUERY = "SELECT * FROM book";
	private static final String UPDATE_QUERY = "UPDATE book SET code=? WHERE id=?";
	private static final String REMOVE_QUERY = "DELETE FROM book WHERE id = ?";
	private static final String REMOVE_ALL_QUERY = "DELETE FROM book WHERE 1";

	//Création d'un book
	public void create( Book bookUser ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement statement = connection.prepareStatement( INSERT_QUERY);
		statement.setString( 1, bookUser.getId() );
		statement.setString( 2, bookUser.getCode() );
		try {
			statement.executeUpdate();
		} catch (Exception e) {
			System.err.println("Le book avec l'id : " + bookUser.getId() +" est d�ja pr�sent dans la base de donn�es");
		}
		statement.close();
		connection.close();
		ContactDAO monContactDao = new ContactDAO();
		for (HashMap.Entry<String, Contact> entry : bookUser.getContacts().entrySet())
		{
			try {
				monContactDao.create(entry.getValue(), bookUser.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//Trouver un book avec son id
	public Book findById( String idBook ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_BY_ID_QUERY );
		st.setString(1, idBook);
		ResultSet rs = st.executeQuery();
		Book book = null;

		if ( rs.next() ) {
			book = new Book();
			book.setId(rs.getString("id"));
			book.setCode(rs.getString("code"));
		}
		rs.close();
		st.close();
		connection.close();

		ContactDAO monContactDao = new ContactDAO();
		List<Contact> contacts = monContactDao.findAllByCodeBook(book);
		for (Contact contact : contacts) {
			book.addContact(contact);
		}
		return book;
	}

	//Afficher tout les books
	@Override
	public List<Book> findAll() throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_ALL_QUERY );
		ResultSet rs = st.executeQuery();
		List<Book> book = new ArrayList<>();
		Book unBook;
		ContactDAO monContactDao = new ContactDAO();

		while ( rs.next() ) {
			unBook = new Book();
			unBook.setId(rs.getString("id"));
			unBook.setCode(rs.getString("code"));
			List<Contact> mesContacts = monContactDao.findAllByCodeBook(unBook);
			for(Contact unContact : mesContacts) {
				unBook.addContact(unContact);
			}
			book.add(unBook);
		}
		rs.close();
		st.close();
		connection.close();
		return book;
	}

	//Mise à jour d'un book
	@Override
	public Book update( Book b ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( UPDATE_QUERY);
		st.setString( 1, b.getCode() );
		st.setString( 2, b.getId() );
		st.executeUpdate();
		st.close();
		connection.close();
		return findById( b.getId() );
	}

	//Supression d'un book
	public void remove( String id ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_QUERY);
		st.setString( 1, id );
		st.executeUpdate();
		st.close();
		connection.close();
	}

}
