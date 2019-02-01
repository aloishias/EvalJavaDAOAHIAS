package fr.epsi.book.dal;

import java.sql.SQLException;
import java.util.List;

import fr.epsi.book.domain.Contact;

public interface IDAO<E, ID> {

	public List<E> findAll() throws SQLException;

	public E update( E o ) throws SQLException;
}
