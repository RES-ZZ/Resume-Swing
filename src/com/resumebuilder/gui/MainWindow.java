package com.resumebuilder.gui;

import com.resumebuilder.model.Resume;
import com.resumebuilder.dao.DatabaseConnection;
import java.awt.Desktop;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;

public class MainWindow extends JFrame {
	private JPanel mainPanel;
	private JPanel inputPanel;

	// Define new color scheme
	private static final Color BACKGROUND_COLOR = new Color(240, 240, 240); // Light gray
	private static final Color ACCENT_COLOR = new Color(0, 123, 255); // Blue
	private static final Color TEXT_COLOR = new Color(33, 37, 41); // Dark gray
	private static final Color INPUT_BACKGROUND = Color.WHITE;

	private Map<String, JTextField> textFields = new HashMap<>();
	private JTextArea summaryArea;

	public MainWindow() {
		setTitle("Resume Builder");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(BACKGROUND_COLOR);
		add(mainPanel);

		setupMenuBar();
		addResumeScorePanel();

		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBackground(BACKGROUND_COLOR);
		mainPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);

		addInputFields();
		addSubmitButton();
	}

	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		// Add menu items as needed
	}

	private void addResumeScorePanel() {
		JPanel scorePanel = new JPanel();
		scorePanel.setBackground(BACKGROUND_COLOR);
		mainPanel.add(scorePanel, BorderLayout.NORTH);
		// Add score components as needed
	}

	private void addInputFields() {
		JPanel fieldsPanel = new JPanel(new GridBagLayout());
		fieldsPanel.setBackground(BACKGROUND_COLOR);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);

		String[] labels = {"Job Title", "First Name", "Last Name", "Email", "Phone", "Country", "City"};
		int gridy = 0;

		for (String label : labels) {
			gbc.gridx = 0;
			gbc.gridy = gridy;
			JLabel jLabel = new JLabel(label);
			jLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
			jLabel.setForeground(TEXT_COLOR);
			fieldsPanel.add(jLabel, gbc);

			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			JTextField textField = new JTextField(20);
			textField.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
			textField.setBackground(INPUT_BACKGROUND);
			textField.setForeground(TEXT_COLOR);
			textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			fieldsPanel.add(textField, gbc);

			textFields.put(label, textField);

			gridy++;
		}

		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		JLabel summaryLabel = new JLabel("Professional Summary");
		summaryLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
		summaryLabel.setForeground(TEXT_COLOR);
		fieldsPanel.add(summaryLabel, gbc);

		gridy++;
		gbc.gridy = gridy;
		summaryArea = new JTextArea(10, 30);
		summaryArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
		summaryArea.setLineWrap(true);
		summaryArea.setWrapStyleWord(true);
		summaryArea.setBackground(INPUT_BACKGROUND);
		summaryArea.setForeground(TEXT_COLOR);
		summaryArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		fieldsPanel.add(new JScrollPane(summaryArea), gbc);

		inputPanel.add(fieldsPanel, BorderLayout.NORTH);
	}

	private void addSubmitButton() {
		JButton submitButton = new JButton("Generate Resume");
		submitButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
		submitButton.setBackground(ACCENT_COLOR);
		submitButton.setForeground(Color.WHITE);
		submitButton.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
		submitButton.setFocusPainted(false);
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateResume();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(BACKGROUND_COLOR);
		buttonPanel.add(submitButton);

		inputPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void generateResume() {
		Resume resume = new Resume();
		resume.setJobTitle(textFields.get("Job Title").getText());
		resume.setFirstName(textFields.get("First Name").getText());
		resume.setLastName(textFields.get("Last Name").getText());
		resume.setEmail(textFields.get("Email").getText());
		resume.setPhone(textFields.get("Phone").getText());
		resume.setCountry(textFields.get("Country").getText());
		resume.setCity(textFields.get("City").getText());
		resume.setProfessionalSummary(summaryArea.getText());

		DatabaseConnection dao = new DatabaseConnection();
		try {
			dao.saveResume(resume);
			generatePDF(resume);
			JOptionPane.showMessageDialog(this, "Resume generated, saved to database, and exported as PDF successfully!");
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Error saving resume to database: " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void generatePDF(Resume resume) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("resume.pdf"));
		document.open();

		com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
		com.itextpdf.text.Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
		com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

		document.add(new Paragraph(resume.getFirstName() + " " + resume.getLastName(), titleFont));
		document.add(new Paragraph(resume.getJobTitle(), headingFont));
		document.add(new Paragraph("\n"));

		document.add(new Paragraph("Contact Information", headingFont));
		document.add(new Paragraph("Email: " + resume.getEmail(), normalFont));
		document.add(new Paragraph("Phone: " + resume.getPhone(), normalFont));
		document.add(new Paragraph("Location: " + resume.getCity() + ", " + resume.getCountry(), normalFont));
		document.add(new Paragraph("\n"));

		document.add(new Paragraph("Professional Summary", headingFont));
		document.add(new Paragraph(resume.getProfessionalSummary(), normalFont));
		String filePath = System.getProperty("user.dir") + "/resume.pdf";
		document.close();
		JOptionPane.showMessageDialog(null, "PDF created successfully!\nFile saved at: " + filePath);
		document.close();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainWindow().setVisible(true);
			}
		});
	}
}