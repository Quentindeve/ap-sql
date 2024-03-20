package ap.coralduchateldutilleul;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.OptionalInt;

public class TestMain {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		SQLBackend backend = new SQLBackend("jdbc:mysql://127.0.0.1:3306/mission7", "root", "root");
		Optional<TaskState> taskState = Optional.of(TaskState.WAITING);
		OptionalInt responsible = OptionalInt.of(1);
		OptionalInt priority = OptionalInt.of(1);
		ResultSet result = backend.requestTaches(taskState, responsible, priority);
		
		ResultSet result2 = backend.getTaches(OptionalInt.of(1), OptionalInt.of(1), OptionalInt.of(2));
		
		while (result.next()) {
			System.out.println(result.getString("description_tache"));
		}
	}

}
