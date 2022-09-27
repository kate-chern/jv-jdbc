package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mate.jdbc.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {
    @Override
    public void create(Manufacturer manufacturer) {
        String sqlQuery = "INSERT INTO manufacturers(name, country) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, manufacturer.getName());
            preparedStatement.setString(2, manufacturer.getCountry());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Insert values to DB failed", e);
        }
    }

    @Override
    public void get(Long id) {
        String sqlQuery = "SELECT * FROM manufacturers " + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)
        ) {
            Manufacturer manufacturer;
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                manufacturer = new Manufacturer();
                String name = resultSet.getString("name");
                String country = resultSet.getString("country");
                manufacturer.setId(id);
                manufacturer.setName(name);
                manufacturer.setCountry(country);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Select values from DB failed", e);
        }
    }

    @Override
    public void getAll() {
        String sqlQuery = "SELECT * FROM manufacturers " + "WHERE is_deleted = FALSE;";
        List<Manufacturer> allManufacturers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement preparedStatement = connection.createStatement()) {
            ResultSet resultSet = preparedStatement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                String name = resultSet.getString("name");
                String country = resultSet.getString("country");
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(id);
                manufacturer.setName(name);
                manufacturer.setCountry(country);
                allManufacturers.add(manufacturer);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Select all values from DB failed", e);
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String sqlQuery = "UPDATE manufacturers SET name = ?, country = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, manufacturer.getName());
            preparedStatement.setString(2, manufacturer.getCountry());
            preparedStatement.setLong(3, manufacturer.getId());
            preparedStatement.executeUpdate();
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Update values from DB failed", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "UPDATE manufacturers SET is_deleted = TRUE "
                    + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Delete values from DB failed", e);
        }
    }
}
