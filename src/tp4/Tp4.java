package tp4;
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


    private static JTextField[] inputTextFields = new JTextField[11]; // input fields

    public static void main(String[] args) {

        // Main Frame
        JFrame frame = new JFrame("Injection Molding Machine Control ");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("C:\\Users\\bensa\\Documents\\NetBeansProjects\\Tp4\\src\\tp4\\1.png").getImage());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Configuration Panel
        JPanel config = new JPanel();
        config.setLayout(new GridLayout(0, 2, 10, 10));
        config.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder("Configuration")));

       //labels and input fields
        String[] labels = {
            "Melt Temperature (°C):",
            "Mold Temperature (°C):",
            "Eject Temperature (°C):",
            "Injection Speed (mm/s):",
            "Injection Pressure (MPa):",
            "Mold Gate's opening Length (mm):",
            "Mold Gate's opening Width (mm):",
            "Length (cm):",
            "Width (cm):",
            "Height (cm):",
            "Thickness (cm):"
        };

        for (int i = 0; i < labels.length; i++) {
            inputTextFields[i] = new JTextField();
            config.add(new JLabel(labels[i]));
            config.add(inputTextFields[i]);

            if (i == 2) {
                config.add(new JLabel("Machine Parameters:"));
                config.add(new JLabel(""));
            } else if (i == 6) { 
                config.add(new JLabel("Piece Size:"));
                config.add(new JLabel(""));
            }
        }
        

        // Material Type
        String[] materials = {"ABS", "PP", "PC", "Nylon", "PLA", "Ceramic", "Porcelain", "Alumina", "SiliconCarbide", "Zirconia"};
        JComboBox<String> materialComboBox = new JComboBox<>(materials);
        config.add(new JLabel("Material Type:"));
        config.add(materialComboBox);
        
        //Calculate button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
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
                ImageIcon backgroundImage = new ImageIcon("C:\\Users\\bensa\\Documents\\NetBeansProjects\\Tp4\\src\\tp4\\2.png");
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
                double thermalDiffusivity = MaterialParameters(selectedMaterial, inputTextFields[0], inputTextFields[1], inputTextFields[2], errorLabel);
            }
        });

        // Calculation button action
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double meltTemp = Double.parseDouble(inputTextFields[0].getText());
                    double moldTemp = Double.parseDouble(inputTextFields[1].getText());
                    double ejectTemp = Double.parseDouble(inputTextFields[2].getText());
                    double injectionSpeed = Double.parseDouble(inputTextFields[3].getText());
                    double injectionPressure = Double.parseDouble(inputTextFields[4].getText());
                    double openingLength = Double.parseDouble(inputTextFields[5].getText());
                    double openingWidth = Double.parseDouble(inputTextFields[6].getText());
                    double length = Double.parseDouble(inputTextFields[7].getText())*10;
                    double width = Double.parseDouble(inputTextFields[8].getText())*10;
                    double height = Double.parseDouble(inputTextFields[9].getText())*10;
                    double thickness = Double.parseDouble(inputTextFields[10].getText())*10;

                    String selectedMaterial = (String) materialComboBox.getSelectedItem();
                    double thermalDiffusivity = MaterialParameters(selectedMaterial, inputTextFields[0], inputTextFields[1], inputTextFields[2], errorLabel);

                    errorLabel.setVisible(false);

                    // Check if thermalDiffusivity was successfully retrieved from materials.txt
                    if (thermalDiffusivity <= 0) {
                        errorLabel.setText("Material parameters not found or thermal diffusivity is zero/negative! Please select a valid material.");
                        errorLabel.setVisible(true);
                        return;
                    }

                    // Validate all numerical inputs
                    if (meltTemp <= 0 || moldTemp <= 0 || ejectTemp <= 0) {
                        errorLabel.setText("Error: Temperatures (Melt, Mold, Eject) must be positive values.");
                        errorLabel.setVisible(true);
                        return;
                    }
                    if (injectionSpeed <= 0) {
                        errorLabel.setText("Error: Injection Speed must be a positive value.");
                        errorLabel.setVisible(true);
                        return;
                    }
                    if (injectionPressure <= 0) {
                        errorLabel.setText("Error: Injection Pressure must be a positive value.");
                        errorLabel.setVisible(true);
                        return;
                    }
                    if (openingLength <= 0 || openingWidth <= 0) {
                        errorLabel.setText("Error: Opening Length and Width must be positive values.");
                        errorLabel.setVisible(true);
                        return;
                    }
                    if (length <= 0 || width <= 0 || height <= 0 || thickness <= 0) {
                        errorLabel.setText("Error: Piece dimensions (Length, Width, Height, Thickness) must be positive values.");
                        errorLabel.setVisible(true);
                        return;
                    }

                    // Validate logical temperature relationships
                    if (meltTemp <= moldTemp) {
                        errorLabel.setText("Error: Melt Temperature must be greater than Mold Temperature.");
                        errorLabel.setVisible(true);
                        return;
                    }
                    if (moldTemp <= ejectTemp) {
                        errorLabel.setText("Error: Mold Temperature must be greater than Eject Temperature.");
                        errorLabel.setVisible(true);
                        return;
                    }  
                    
                    // Calculations
                    double coolingTime = ((thickness * thickness) / (Math.PI * Math.PI * thermalDiffusivity)) * Math.log((meltTemp - ejectTemp) / (moldTemp - ejectTemp));
                    double injectionTime = (length * width * height) / ((openingLength * openingWidth) * injectionSpeed);
                    double ejectionTime = 2.0;
                    
                    // --- Additional check for valid calculation results ---
                    if (Double.isNaN(coolingTime) || Double.isInfinite(coolingTime) || coolingTime < 0 ||
                        Double.isNaN(injectionTime) || Double.isInfinite(injectionTime) || injectionTime < 0) {
                        errorLabel.setText("Error: Calculation resulted in an invalid time value. Check your inputs (e.g., temperatures resulting in log of zero/negative, or zero speed/area).");
                        errorLabel.setVisible(true);
                        return;
                    }

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
                    dashboard.add(errorLabel);
                    
                    // Real Time Simulation
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                    }
                    timerLabel.setText("Time: 0s");
                    cyclePartLabel.setText("Cycle Finished Parts: 0");
                    progressBar.setValue(0);

                    timer = new Timer(1000, new ActionListener() {
                        int time = 0;
                        int totalCycleTime = (int) Math.round(injectionTime + coolingTime + ejectionTime); // Round to nearest int

                        public void actionPerformed(ActionEvent e) {
                            time++;
                            timerLabel.setText("Time: " + time + "s");

                            double currentProgress = 0;
                            if (totalCycleTime > 0) {
                                currentProgress = (double)time / totalCycleTime;
                            }
                            progressBar.setValue((int) (currentProgress * 100));


                            if (time <= injectionTime) {
                                cyclePartLabel.setText("Cycle Part: Injection");
                            } else if (time <= injectionTime + coolingTime) {
                                cyclePartLabel.setText("Cycle Part: Cooling");
                            } else if (time <= totalCycleTime) {
                                cyclePartLabel.setText("Cycle Part: Ejection");
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
                    errorLabel.setText("Please enter valid numbers in all fields.");
                    errorLabel.setVisible(true);
                } catch (ArithmeticException ex) {
                    errorLabel.setText("Error during calculation: " + ex.getMessage() + ". Check for zero or negative values in inputs.");
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
    private static double MaterialParameters(String material, JTextField meltTempField, JTextField moldTempField, JTextField ejectTempField, JLabel errorLabel) {
        try (BufferedReader reader = new BufferedReader(new FileReader("materials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(":");
                if (fields.length < 5) { 
                    System.err.println("Warning: Malformed line in materials.txt (too few fields): " + line);
                    continue;
                }

                if (fields[0].equals(material)) {
                    try {
                        // Set the text fields bydefault
                        meltTempField.setText(fields[1]);
                        moldTempField.setText(fields[2]);
                        ejectTempField.setText(fields[3]);
                        double diffusivity = Double.parseDouble(fields[4]);
                        
                        // Validate diffusivity from file
                        if (diffusivity <= 0) {
                            errorLabel.setText("Error: Thermal diffusivity for " + material + " in materials.txt must be positive.");
                            errorLabel.setVisible(true);
                            return 0;
                        }
                        errorLabel.setVisible(false);
                        return diffusivity;

                    } catch (NumberFormatException e) {
                        errorLabel.setText("Error: Invalid number format for " + material + " in materials.txt.");
                        errorLabel.setVisible(true);
                        return 0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error reading materials.txt: " + e.getMessage() + ". Ensure file exists and is accessible.");
            errorLabel.setVisible(true);
        }
        errorLabel.setText("Material parameters for " + material + " not found!");
        errorLabel.setVisible(true);
        return 0;
    }
}