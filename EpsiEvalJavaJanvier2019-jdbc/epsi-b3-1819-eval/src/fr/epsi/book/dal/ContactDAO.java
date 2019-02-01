package fr.epsi.book.dal;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;
import fr.epsi.book.domain.Contact.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO implements IDAO<Contact, Long> {

	private static final String INSERT_QUERY = "INSERT INTO contact (id, name, email, phone, type_var, type_num, code_book) values (?, ?,?,?,?,?,?)";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM contact WHERE id=?";
	private static final String FIND_ALL_QUERY = "SELECT * FROM contact";
	private static final String UPDATE_QUERY = "UPDATE contact SET name=?,email=?,phone=?,type_var=?,type_num=? WHERE id=?";
	private static final String REMOVE_QUERY = "DELETE FROM contact WHERE id = ?";
	private static final String FIND_ALL_BY_CODE_BOOK_QUERY = "SELECT * FROM contact WHERE code_book=?";
	private static final String REMOVE_ALL_QUERY = "DELETE FROM contact WHERE 1";

	//Création d'un contact
	public void create( Contact c, String idBook ) throws SQLException {

		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( INSERT_QUERY);
		st.setString( 1, c.getId() );
		st.setString( 2, c.getName() );
		st.setString( 3, c.getEmail() );
		st.setString( 4, c.getPhone() );
		st.setString( 5, c.getType().getValue() );
		st.setInt( 6, c.getType().ordinal() );
		st.setString( 7, idBook );
		try {
			st.executeUpdate();
		} catch (Exception e) {
			System.err.println("Le contact avec l'id : " + c.getId() +" est d�ja pr�sent dans la base de donn�es");
		}
		st.close();
		connection.close();
	}

	//Trouver un contact avec son id
	public Contact findById( String idContact ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_BY_ID_QUERY );
		st.setString(1, idContact);
		ResultSet rs = st.executeQuery();
		Contact contact = null;

		if ( rs.next() ) {
			contact = new Contact();
			contact.setId(rs.getString("id"));
			contact.setName(rs.getString("name"));
			contact.setEmail(rs.getString("email"));
			contact.setPhone(rs.getString("phone"));
			if(rs.getInt("type_num") == 0) {
				contact.setType(Type.PERSO);
			}else {
				contact.setType(Type.PRO);
			}
		}
		rs.close();
		st.close();
		connection.close();
		return contact;
	}

	//Lister tout els contact
	@Override
	public List<Contact> findAll() throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_ALL_QUERY );
		ResultSet rs = st.executeQuery();
		List<Contact> contact = new ArrayList<>();
		Contact unContact = new Contact();

		while ( rs.next() ) {
			unContact.setId(rs.getString("id"));
			unContact.setName(rs.getString("name"));
			unContact.setEmail(rs.getString("email"));
			unContact.setPhone(rs.getString("phone"));
			if(rs.getInt("type_num") == 0) {
				unContact.setType(Type.PERSO);
			}else {
				unContact.setType(Type.PRO);
			}
			contact.add(unContact);
		}
		rs.close();
		st.close();
		connection.close();
		return contact;
	}

	//Mise à jour d'un contact
	@Override
	public Contact update( Contact c ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( UPDATE_QUERY);
		st.setString( 1, c.getName() );
		st.setString( 2, c.getEmail() );
		st.setString( 3, c.getPhone() );
		st.setString( 4, c.getType().getValue() );
		st.setInt( 5, c.getType().ordinal() );
		st.setString( 6, c.getId() );
		st.executeUpdate();
		st.close();
		connection.close();
		return findById(c.getId());
	}

	//Suppression d'un contact
	public void remove( Contact c ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_QUERY);
		st.setString( 1, c.getId() );
		st.executeUpdate();
		st.close();
		connection.close();
	}

	//Afficher tout les contact d'un book
	public List<Contact> findAllByCodeBook( Book b ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_ALL_BY_CODE_BOOK_QUERY);
		st.setString( 1, b.getCode());
		ResultSet rs = st.executeQuery();

		List<Contact> contact = new ArrayList<>();
		Contact unContact;

		while ( rs.next() ) {
			unContact = new Contact();
			unContact.setId(rs.getString("id"));
			unContact.setName(rs.getString("name"));
			unContact.setEmail(rs.getString("email"));
			unContact.setPhone(rs.getString("phone"));
			if(rs.getInt("type_num") == 0) {
				unContact.setType(Type.PERSO);
			}else {
				unContact.setType(Type.PRO);
			}
			contact.add(unContact);
		}

		st.close();
		connection.close();
		return contact;
	}

	//Supprimer tout les contacts
	public void removeAll() throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_ALL_QUERY);
		st.executeUpdate();
		st.close();
		connection.close();
	}
}


