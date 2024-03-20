package ap.coralduchateldutilleul;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.OptionalInt;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField descriptionField;
	private JComboBox responsibleComboBox;
	private JLabel lblNewLabel_1;
	private JTable resultsTable;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws SQLException 
	 */
	public MainFrame() throws SQLException {
		SQLBackend db = new SQLBackend("jdbc:mysql://127.0.0.1:3306/mission7", "root", "root");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		descriptionField = new JTextField();
		descriptionField.setBounds(64, 91, 195, 21);
		contentPane.add(descriptionField);
		descriptionField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Description de la tâche");
		lblNewLabel.setBounds(64, 62, 153, 17);
		contentPane.add(lblNewLabel);
		
		responsibleComboBox = new JComboBox();
		responsibleComboBox.setBounds(65, 153, 195, 26);
		var result = db.getAll("responsables");
		while (result.next()) {
			responsibleComboBox.addItem(result.getString("nom_prenom"));
		}
		
		contentPane.add(responsibleComboBox);
		
		lblNewLabel_1 = new JLabel("Responsable de la tâche");
		lblNewLabel_1.setBounds(65, 124, 195, 17);
		contentPane.add(lblNewLabel_1);
		
		JButton searchButton = new JButton("Chercher");
		searchButton.setBounds(167, 258, 93, 27);
		contentPane.add(searchButton);
		
		JLabel lblNewLabel_2 = new JLabel("Priorité");
		lblNewLabel_2.setBounds(65, 196, 195, 17);
		contentPane.add(lblNewLabel_2);
		
		JComboBox priorityComboBox = new JComboBox();
		result = db.getAll("priorite");
		while (result.next()) {
			priorityComboBox.addItem(result.getString("description"));
		}
		priorityComboBox.setBounds(64, 220, 195, 26);
		contentPane.add(priorityComboBox);
		
		JButton addButton = new JButton("Ajouter");
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int priority = priorityComboBox.getSelectedIndex() + 1;
				int responsible = responsibleComboBox.getSelectedIndex() + 1;
				String task_description = descriptionField.getText();
				try {
					db.insertTuple(responsible, task_description, priority);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		addButton.setBounds(65, 258, 93, 27);
		contentPane.add(addButton);
		
		resultsTable = new JTable();
		resultsTable.setBounds(331, 62, 1000, 600);
		contentPane.add(resultsTable);
		
		JComboBox idBox = new JComboBox();
		idBox.setBounds(64, 348, 195, 26);
		contentPane.add(idBox);
		
		JLabel lblNewLabel_3 = new JLabel("ID");
		lblNewLabel_3.setBounds(64, 324, 60, 17);
		contentPane.add(lblNewLabel_3);
		
		JButton modifyButton = new JButton("Modifier");
		modifyButton.setBounds(64, 386, 195, 27);
		contentPane.add(modifyButton);
		
		JLabel lblNewLabel_4 = new JLabel("Statut");
		lblNewLabel_4.setBounds(64, 436, 60, 17);
		contentPane.add(lblNewLabel_4);
		
		JComboBox statutBox = new JComboBox();
		statutBox.setBounds(64, 465, 195, 26);
		contentPane.add(statutBox);
		
		JButton deleteButton = new JButton("Supprimer");
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		deleteButton.setBounds(64, 297, 195, 27);
		contentPane.add(deleteButton);
		
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var priority = OptionalInt.of(priorityComboBox.getSelectedIndex() + 1);
				var responsible = OptionalInt.of(db.getResponsablesId().get((String)responsibleComboBox.getSelectedItem()));

				
				try {
					String[] columns = {"ID", "Priorité", "Responsable", "Statut", "Description"};
					var model = new DefaultTableModel();
					for (String str : columns) {
						model.addColumn(str);
					}
					
					var results = db.getTaches(priority, responsible, OptionalInt.empty());
					while (results.next()) {
						var id = results.getInt("id");
						var priorite = results.getString("priorite");;
						var responsable = results.getString("responsable");
						var statut = results.getString("statut");
						var description = results.getString("description_tache");
						
						Object[] line = {id, responsable, priorite, statut, description};
						model.addRow(line);
					}
					resultsTable.setModel(model);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		var rSet = db.getTaches(OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty());
		while (rSet.next()) {
			idBox.addItem(rSet.getInt("id"));
		}
		
		rSet = db.getAll("statut_tache");
		while (rSet.next()) {
			statutBox.addItem(rSet.getString("description"));
		}
		
		modifyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int id = (int)idBox.getSelectedItem();
				int priority = priorityComboBox.getSelectedIndex() + 1;
				System.out.println(priority);
				int responsible = db.getResponsablesId().get((String)responsibleComboBox.getSelectedItem());
				int status = db.getStatutId().get((String)statutBox.getSelectedItem());
				db.modifyTache(id, priority, responsible, status);
			}
		});
		
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int id = (int)idBox.getSelectedItem();
				db.deleteTache(id);
			}
		});
	}
}
