import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FindAGame {

    private static final Map<Specs, Object> userSpecs = new HashMap<Specs, Object>();
    private static final JComboBox<Object> subGenreSelection = new JComboBox<>();
    private static final JLabel feedback = new JLabel("");
    private static Game game = null;
    private static Genre genre = null;
    private static GameSpecs gs = null;
    private static Object[] subgenre = {};
    private static double minP = -1;
    private static double maxP = -1;
    private static JTextField minPrice = null;
    private static JTextField maxPrice = null;
    JFrame frame = new JFrame("The Greek Geek's Game Finder");
    JPanel panelCont = new JPanel();
    JPanel searchPanel = new JPanel();
    JPanel gameSelectionPanel = new JPanel();
    JPanel submitOrderPanel = new JPanel();
    JButton searchButton = new JButton("Search");
    JButton searchAgainButton = new JButton("Search Again");
    JButton submitOrderButton = new JButton("Submit Order");
    CardLayout cl = new CardLayout();

    public FindAGame() {
        AllGames ag = LoadGameData();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("./icon.jpg").getImage());
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelCont.setLayout(cl);
        JPanel genrePanel = new JPanel();
        genrePanel.setPreferredSize(new Dimension(480, 140));
        genrePanel.setLayout(new GridLayout(2, 1, 10, 10));
        genrePanel.setBorder(new CompoundBorder(new EmptyBorder(20, 10, 10, 10), BorderFactory.createTitledBorder("Please select your preferred genre & sub-genre")));

        try {
            JComboBox<Object> genreSelection = new JComboBox<>(Genre.values());
            genreSelection.insertItemAt("Select genre", 0);
            genreSelection.setSelectedIndex(0);
            genreSelection.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 0, 10), BorderFactory.createCompoundBorder()));
            genreSelection.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED && !genreSelection.getSelectedItem().toString().equals("Select genre")) {

                        genre = Genre.valueOf(genreSelection.getSelectedItem().toString().toLowerCase().replace(" ", "_"));
                        subgenre = ag.getAllSubGenres(genre).toArray();
                        if (subGenreSelection.getItemCount() == 1) {
                            for (Object obj : subgenre) {
                                subGenreSelection.addItem(obj);
                            }
                        } else {
                            subGenreSelection.removeAllItems();
                            subGenreSelection.insertItemAt("Select sub-genre", 0);
                            subGenreSelection.setSelectedIndex(0);
                            for (Object obj : subgenre) {
                                subGenreSelection.addItem(obj);
                            }
                        }
                    }
                    else if(genreSelection.getSelectedItem().toString().equals("Select genre")){
                    subGenreSelection.setSelectedIndex(0);
                }
            });
            subGenreSelection.insertItemAt("Select sub-genre", 0);
            subGenreSelection.setSelectedIndex(0);
            subGenreSelection.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10), BorderFactory.createCompoundBorder()));
            genrePanel.add(genreSelection);
            genrePanel.add(subGenreSelection);
            JComboBox<Object> platformSelection = new JComboBox<>(Platform.values());
            platformSelection.insertItemAt("Select platform", 0);
            platformSelection.setSelectedIndex(0);
            platformSelection.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createCompoundBorder()));
            JPanel platformPanel = new JPanel();
            platformPanel.setPreferredSize(new Dimension(480, 90));
            platformPanel.setLayout(new GridLayout(1, 1, 20, 20));
            platformPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("Please select your preferred platform")));
            platformPanel.add(platformSelection);

            JLabel min = new JLabel("Min.");
            JLabel max = new JLabel("Max.");
            minPrice = new JTextField("0.00", 5);
            maxPrice = new JTextField("0.00", 5);
            JPanel matchedPanel = new JPanel();
            JPanel selectionPanel = new JPanel();
            JPanel myPanel = new JPanel();
            JPanel resultPanel = new JPanel();
            feedback.setFont(new Font("", Font.ITALIC, 12));
            searchButton.setPreferredSize(new Dimension(460, 30));
            searchButton.setEnabled(false);
            searchButton.addActionListener(e -> {
                userSpecs.put(Specs.Genre, genre);
                if(!subGenreSelection.getSelectedItem().toString().toLowerCase().equals("na")) {
                    userSpecs.put(Specs.Subgenre, subGenreSelection.getSelectedItem());
                }
                userSpecs.put(Specs.Platform, Platform.valueOf(platformSelection.getSelectedItem().toString().replace(" ", "_")));
                gs = new GameSpecs(Double.parseDouble(maxPrice.getText()), Double.parseDouble(minPrice.getText()), userSpecs);
                game = new Game(null, 0, 0, gs);
                ArrayList<Game> matchedGames = ag.findGames(game);
                if (matchedGames.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Unfortunately no games met your criteria.", "The Greek Geek's Game Finder", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    cl.show(panelCont, "2");
                    String S = "";
                    for (Game g : matchedGames) {
                        S = S + g.getDescription(game.getGameSpecs().getGSpecs().keySet().stream().toArray(Specs[]::new));
                    }
                    Map<Object, Game> map = new HashMap<Object, Game>();
                    for (Game g : matchedGames) {
                        map.put(g.getTitle(), g);
                    }
                    JTextArea gameMatchesField = new JTextArea();
                    gameMatchesField.append(S);
                    gameMatchesField.setEditable(false);
                    JScrollPane areaScrollPane = new JScrollPane(gameMatchesField);
                    areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    areaScrollPane.setPreferredSize(new Dimension(250, 150));

                    matchedPanel.setPreferredSize(new Dimension(480, 300));
                    matchedPanel.setLayout(new GridLayout(1, 1, 20, 20));
                    matchedPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("The following games meet your criteria:")));
                    matchedPanel.add(areaScrollPane);
                    JComboBox selectionBox = new JComboBox(map.keySet().toArray());
                    selectionBox.insertItemAt("Select game", 0);
                    selectionBox.setSelectedIndex(0);
                    selectionBox.setPreferredSize(new Dimension(340, 50));
                    selectionBox.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createCompoundBorder()));
                    selectionBox.addItemListener(e2 -> {
                        if (e2.getStateChange() == ItemEvent.SELECTED) {
                            cl.show(panelCont, "3");
                            JTextArea selectedGame = new JTextArea();
                            selectedGame.append(map.get(selectionBox.getSelectedItem()).getDescription());
                            selectedGame.setEditable(false);


                            resultPanel.setPreferredSize(new Dimension(480, 180));
                            resultPanel.setLayout(new GridLayout(1, 1, 20, 20));
                            resultPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 0, 10), BorderFactory.createTitledBorder("To place an order for "+selectionBox.getSelectedItem().toString()+" fill in the form below")));
                            resultPanel.add(selectedGame);
                            JLabel nameLabel = new JLabel("Enter your full name");
                            JLabel phoneNumberLabel = new JLabel("Enter your phone number");
                            JLabel messageLabel = new JLabel("Do you want to leave a message?");
                            JLabel feedback = new JLabel("");
                            feedback.setFont(new Font("", Font.ITALIC, 12));
                            JTextField Name = new JTextField();
                            JTextField PhoneNumber = new JTextField();
                            JTextArea Message = new JTextArea();
                            JScrollPane areaScrollPane2 = new JScrollPane(Message);
                            areaScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                            //areaScrollPane2.setPreferredSize(new Dimension(250, 180));
                            nameLabel.setPreferredSize(new Dimension(420, 20));
                            phoneNumberLabel.setPreferredSize(new Dimension(420, 20));
                            messageLabel.setPreferredSize(new Dimension(420, 20));
                            Name.setPreferredSize(new Dimension(420, 20));
                            PhoneNumber.setPreferredSize(new Dimension(420, 20));
                            areaScrollPane2.setPreferredSize(new Dimension(420, 40));
                            int Count =1;
                            submitOrderButton.addActionListener(e3 -> {
                                if (Name.getText().length() > 1 && PhoneNumber.getText().length() >= 10) {
                                    feedback.setText("");
                                    Geek g = new Geek(Name.getText(), PhoneNumber.getText(), Message.getText());
                                    try {
                                        FindAGame.submitOrder(g, map.get(selectionBox.getSelectedItem()));
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    for(int i=0;i<Count;i++){
                                        JOptionPane.showMessageDialog(null, "Message Sent!\n\nOne of our friendly staff will be in touch shortly.", "The Greek Geek's Game Finder", JOptionPane.INFORMATION_MESSAGE);
                                    }

                                    cl.show(panelCont,"1");
                                    matchedPanel.removeAll();
                                    selectionPanel.removeAll();
                                    myPanel.removeAll();
                                    resultPanel.removeAll();
                                    genreSelection.setSelectedIndex(0);
                                    platformSelection.setSelectedIndex(0);
                                    minPrice.setText("0.00");
                                    maxPrice.setText("0.00");
                                    Name.setText("");
                                    PhoneNumber.setText("");
                                    Message.setText("");
                                } else {
                                    if (Name.getText().length() == 0 && PhoneNumber.getText().length() > 11) {
                                        feedback.setText("Please enter a valid name");
                                        feedback.setForeground(Color.RED);
                                        Name.selectAll();
                                        Name.requestFocus();
                                    } else if (Name.getText().length() != 0 && PhoneNumber.getText().length() < 10) {
                                        feedback.setText("Please enter a valid phone number");
                                        feedback.setForeground(Color.RED);
                                        PhoneNumber.selectAll();
                                        PhoneNumber.requestFocus();
                                    } else {
                                        feedback.setText("Please enter valid phone number and name");
                                        feedback.setForeground(Color.RED);
                                        PhoneNumber.selectAll();
                                        PhoneNumber.requestFocus();
                                    }
                                }

                            });
                            submitOrderButton.setPreferredSize(new Dimension(420, 30));
                            submitOrderButton.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createCompoundBorder()));


                            myPanel.setPreferredSize(new Dimension(480, 270));
                            myPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Customer Data:")));
                            myPanel.add(feedback, BorderLayout.CENTER);
                            myPanel.add(nameLabel, BorderLayout.CENTER);
                            myPanel.add(Name, BorderLayout.CENTER);
                            myPanel.add(phoneNumberLabel, BorderLayout.CENTER);
                            myPanel.add(PhoneNumber, BorderLayout.CENTER);
                            myPanel.add(messageLabel, BorderLayout.CENTER);
                            myPanel.add(areaScrollPane2);
                            myPanel.add(submitOrderButton, BorderLayout.CENTER);
                            submitOrderPanel.add(resultPanel);
                            submitOrderPanel.add(myPanel);

                        }
                    });

                    searchAgainButton.addActionListener(e1 -> {
                        matchedPanel.removeAll();
                        selectionPanel.removeAll();
                        cl.show(panelCont, "1");
                    });
                    searchAgainButton.setPreferredSize(new Dimension(100, 30));
                    searchAgainButton.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createCompoundBorder()));

                    selectionPanel.setPreferredSize(new Dimension(480, 100));
                    selectionPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("Please select which game you'd like to order:")));
                    selectionPanel.add(selectionBox);
                    selectionPanel.add(searchAgainButton);
                    gameSelectionPanel.add(matchedPanel);
                    gameSelectionPanel.add(selectionPanel);
                }
            });
            minPrice.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    minPrice.selectAll();
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    try {
                        minP = Double.parseDouble(minPrice.getText());
                        if (minP < 0) {
                            feedback.setText("Min amount should be a positive value");
                            feedback.setForeground(Color.RED);
                            minPrice.selectAll();
                            minPrice.requestFocus();
                            searchButton.setEnabled(false);
                        } else {
                            feedback.setText("");
                            maxPrice.requestFocus();
                            searchButton.setEnabled(false);
                        }
                    } catch (NumberFormatException n) {
                        feedback.setText("Please enter a valid amount");
                        feedback.setForeground(Color.RED);
                        minPrice.selectAll();
                        minPrice.requestFocus();
                        searchButton.setEnabled(false);
                    }
                }
            });
            maxPrice.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    maxPrice.selectAll();
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    try {
                        maxP = Double.parseDouble(maxPrice.getText());
                        if (maxP < minP) {
                            feedback.setText("Max amount must be greater than min amount");
                            feedback.setForeground(Color.RED);
                            maxPrice.selectAll();
                            maxPrice.requestFocus();
                            searchButton.setEnabled(false);
                        } else {
                            feedback.setText("");
                            searchButton.setEnabled(true);
                            searchButton.requestFocus();
                        }
                    } catch (NumberFormatException n) {
                        feedback.setText("Please enter a valid amount");
                        feedback.setForeground(Color.RED);
                        maxPrice.selectAll();
                        maxPrice.requestFocus();
                        searchButton.setEnabled(false);
                    }
                }
            });
            JPanel pricePanel = new JPanel();
            pricePanel.setPreferredSize(new Dimension(480, 90));
            pricePanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("Select desired price range")));
            pricePanel.add(min, BorderLayout.CENTER);
            pricePanel.add(minPrice, BorderLayout.CENTER);
            pricePanel.add(max, BorderLayout.CENTER);
            pricePanel.add(maxPrice, BorderLayout.CENTER);
            pricePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            pricePanel.add(feedback, BorderLayout.SOUTH);
            JPanel submitPanel = new JPanel();
            submitPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createCompoundBorder()));
            submitPanel.setPreferredSize(new Dimension(480, 50));
            submitPanel.add(searchButton, BorderLayout.SOUTH);
            searchPanel.add(genrePanel, BorderLayout.CENTER);
            searchPanel.add(platformPanel, BorderLayout.CENTER);
            searchPanel.add(pricePanel, BorderLayout.CENTER);
            searchPanel.add(submitPanel, BorderLayout.CENTER);

            searchPanel.setPreferredSize(new Dimension(500, 450));
            gameSelectionPanel.setPreferredSize(new Dimension(500, 450));
            submitOrderPanel.setPreferredSize(new Dimension(500, 450));

            panelCont.add(searchPanel, "1");
            panelCont.add(gameSelectionPanel, "2");
            panelCont.add(submitOrderPanel, "3");
            cl.show(panelCont, "1");
            frame.add(panelCont);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "You have entered an invalid input!!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FindAGame();
            }
        });
    }

    public static AllGames LoadGameData() {
        String games = LoadFile("./allGames.txt");
        String[] arrOfGames = games.split("\n");
        arrOfGames = Arrays.copyOfRange(arrOfGames, 1, arrOfGames.length);
        AllGames Arrgames = new AllGames();
        for (String a : arrOfGames) {
            Map<Specs, Object> gamespecs = new HashMap<Specs, Object>();
            String[] gameInfo = a.split(",", 7);
            String title = gameInfo[0];
            long product_code = Long.parseLong(gameInfo[1]);
            double price = Double.parseDouble(gameInfo[2]);
            gamespecs.put(Specs.Genre, Genre.valueOf(gameInfo[3].replace(" ", "_")));
            gamespecs.put(Specs.Platform, Platform.valueOf(gameInfo[4].replace(" ", "_")));
            gamespecs.put(Specs.Subgenre, gameInfo[5]);
            gamespecs.put(Specs.Rating, Rating.valueOf(gameInfo[6]).toString());
            GameSpecs gamespec = new GameSpecs(0, 0, gamespecs);
            Game game = new Game(title, product_code, price, gamespec);
            Arrgames.addGame(game);
        }
        return Arrgames;
    }

    public static void submitOrder(Geek geek, Game game) throws IOException {
        String fileName = geek.getFullName().replace(" ", "_") + "_" + game.getProduct_code() + ".txt";
        File file = new File(System.getProperty("user.dir") + "/Orders/" + fileName);
        FileWriter order = new FileWriter(file);
        order.write("Order details: \n");
        order.write("\tName: " + geek.getFullName() + "\n");
        order.write("\tPhone number: " + geek.getPhoneNumber() + "\n");
        order.write("\tgame: " + game.getTitle() + " (" + game.getProduct_code() + ")\n");
        order.write("\tMessage: " + geek.getMessage() + "\n");
        order.close();
    }
    private static String LoadFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(new FileReader(filePath))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder.append(str).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}