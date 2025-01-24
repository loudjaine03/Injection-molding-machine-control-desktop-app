import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Tp4 {
    private static Timer timer;

    public static void main(String[] args) {

        // Main Frame
        JFrame frame = new JFrame("Injection Molding Machine Control ");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("path\\to\\image").getImage());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Configuration Panel
        JPanel config = new JPanel();
        config.setLayout(new GridLayout(0, 2, 10, 10));
        config.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder("Configuration")));

        JLabel t1 = new JLabel("Melt Temperature (°C):");
        JLabel t2 = new JLabel("Mold Temperature (°C):");
        JLabel t3 = new JLabel("Eject Temperature (°C):");
        JLabel s = new JLabel("Injection Speed (mm/s):");
        JLabel p = new JLabel("Injection Pressure (MPa):");
        JLabel l = new JLabel("Length (cm):");
        JLabel w = new JLabel("Width (cm):");
        JLabel h = new JLabel("Height (cm):");
        JLabel l2 = new JLabel("Opening Length (cm):");
        JLabel w2 = new JLabel("Opening Width (cm):");
        JLabel matLabel = new JLabel("Material Type:");
        JTextField txt1 = new JTextField();
        JTextField txt2 = new JTextField();
        JTextField txt3 = new JTextField();
        JTextField txt4 = new JTextField();
        JTextField txt5 = new JTextField();
        JTextField txt6 = new JTextField();
        JTextField txt7 = new JTextField();
        JTextField txt8 = new JTextField();
        JTextField txt9 = new JTextField();
        JTextField txt10 = new JTextField();
        String[] materials = {"ABS", "PP", "PC", "Nylon", "PLA", "Ceramic", "Porcelain", "Alumina", "SiliconCarbide", "Zirconia"};
        JComboBox<String> materialComboBox = new JComboBox<>(materials);
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));

        config.add(new JLabel("Injection Molding Parameters:"));
        config.add(new JLabel(""));
        config.add(t1);
        config.add(txt1);
        config.add(t2);
        config.add(txt2);
        config.add(t3);
        config.add(txt3);
        config.add(new JLabel("Machine Parameters:"));
        config.add(new JLabel(""));
        config.add(s);
        config.add(txt4);
        config.add(p);
        config.add(txt5);
        config.add(l2);
        config.add(txt9);
        config.add(w2);
        config.add(txt10);

        config.add(new JLabel("Piece Size:"));
        config.add(new JLabel(""));
        config.add(l);
        config.add(txt6);
        config.add(w);
        config.add(txt7);
        config.add(h);
        config.add(txt8);

        config.add(matLabel);
        config.add(materialComboBox);
        config.add(calculateButton);

        // Error label
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        // Dashboard Panel
        JPanel dashboard = new JPanel() {
            //background
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("path\\to\\image");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder("Dashboard")));

        JLabel dashboardTitle = new JLabel("Calculation Results");
        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dashboard.add(Box.createVerticalStrut(10));
        dashboard.add(dashboardTitle);
        dashboard.add(Box.createVerticalStrut(20));

        JLabel timerLabel = new JLabel("Time: 0s");
        JLabel cyclePartLabel = new JLabel("Cycle Finished Parts: 0");
        dashboard.add(timerLabel);
        dashboard.add(Box.createVerticalStrut(20));
        dashboard.add(cyclePartLabel);
        dashboard.add(Box.createVerticalStrut(20));
        dashboard.add(errorLabel);


        materialComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedMaterial = (String) materialComboBox.getSelectedItem();
                double thermalDiffusivity = MaterialParameters(selectedMaterial, txt1, txt2, txt3, errorLabel);
            }
        });

        // Calculation button action
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double meltTemp = Double.parseDouble(txt1.getText());
                    double moldTemp = Double.parseDouble(txt2.getText());
                    double ejectTemp = Double.parseDouble(txt3.getText());
                    double injectionSpeed = Double.parseDouble(txt4.getText());
                    double injectionPressure = Double.parseDouble(txt5.getText());
                    double length = Double.parseDouble(txt6.getText());
                    double width = Double.parseDouble(txt7.getText());
                    double height = Double.parseDouble(txt8.getText());
                    double openingLength = Double.parseDouble(txt9.getText());
                    double openingWidth = Double.parseDouble(txt10.getText());
                    String selectedMaterial = (String) materialComboBox.getSelectedItem();
                    double thermalDiffusivity = MaterialParameters(selectedMaterial, txt1, txt2, txt3, errorLabel);
                    if (thermalDiffusivity == 0) {
                        errorLabel.setText("Material parameters not found!");
                        errorLabel.setVisible(true);
                    }
                    if ((meltTemp - ejectTemp) <= 0 || (moldTemp - ejectTemp) <= 0) {
                        errorLabel.setText("Invalid temperature values!");
                        errorLabel.setVisible(true);
                    }
                    // Calculations
                    double coolingTime = ((height * height) / (Math.PI * Math.PI * thermalDiffusivity)) * Math.log((meltTemp - ejectTemp) / (moldTemp - ejectTemp));
                    double injectionTime = (length * width * height) / ((openingLength * openingWidth) * injectionSpeed);
                    double ejectionTime = 2.0;
                    
                    // Update dashboard
                    dashboard.removeAll();
                    dashboard.add(dashboardTitle);
                    dashboard.add(Box.createVerticalStrut(20));
                    dashboard.add(new JLabel("Material: " + selectedMaterial));
                    dashboard.add(Box.createVerticalStrut(20));
                    dashboard.add(new JLabel(String.format("Melt Temperature: %.2f °C", meltTemp)));
                    dashboard.add(new JLabel(String.format("Mold Temperature: %.2f °C", moldTemp)));
                    dashboard.add(new JLabel(String.format("Eject Temperature: %.2f °C", ejectTemp)));
                    dashboard.add(Box.createVerticalStrut(20));
                    dashboard.add(new JLabel(String.format("Injection Time: %.2f seconds", injectionTime)));
                    dashboard.add(new JLabel(String.format("Cooling Time: %.2f seconds", coolingTime)));
                    dashboard.add(new JLabel(String.format("Ejection Time: %.2f seconds", ejectionTime)));
                    dashboard.add(Box.createVerticalStrut(20));
                    dashboard.add(timerLabel);
                    dashboard.add(Box.createVerticalStrut(20));
                    dashboard.add(cyclePartLabel);
                    dashboard.add(Box.createVerticalStrut(20));
                    
                    // Progress Bar
                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setStringPainted(true);
                    dashboard.add(progressBar);
                    
                    // Real Time Simulation
                    timer = new Timer(1000, new ActionListener() {
                        int time = 0;
                        int totalCycleTime = (int) (injectionTime + coolingTime + ejectionTime);

                        public void actionPerformed(ActionEvent e) {
                            time++;
                            timerLabel.setText("Time: " + time + "s");
                            if (time <= injectionTime) {
                                cyclePartLabel.setText("Cycle Part: Injection");
                                progressBar.setValue((int) ((time / injectionTime) * 100));
                            } else if (time <= injectionTime + coolingTime) {
                                cyclePartLabel.setText("Cycle Part: Cooling");
                                progressBar.setValue((int) (((time - injectionTime) / coolingTime) * 100));
                            } else if (time <= totalCycleTime) {
                                cyclePartLabel.setText("Cycle Part: Ejection");
                                progressBar.setValue((int) (((time - injectionTime - coolingTime) / ejectionTime) * 100));
                            } else {
                                timer.stop();
                                cyclePartLabel.setText("Cycle Parts: Completed");
                                progressBar.setValue(100);
                            }
                        }
                    });
                    timer.start();
                    
                    // Frame update
                    frame.repaint();
                    frame.revalidate();
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Please enter valid numbers.");
                    errorLabel.setVisible(true);
                }
            }
        });


        mainPanel.add(config, BorderLayout.WEST);
        mainPanel.add(dashboard, BorderLayout.CENTER);


        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Get material parameters
    private static double MaterialParameters(String material, JTextField txt1, JTextField txt2, JTextField txt3, JLabel errorLabel) {
        try (BufferedReader reader = new BufferedReader(new FileReader("materials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(":");
                if (fields[0].equals(material)) {
                    txt1.setText(fields[1]);
                    txt2.setText(fields[2]);
                    txt3.setText(fields[3]);
                    return Double.parseDouble(fields[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
