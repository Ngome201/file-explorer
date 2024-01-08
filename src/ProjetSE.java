import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.*;
import javax.swing.tree.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.Icon;

public class ProjetSE {
/***********On définit les variables globales de notre programme********************/	
	/*les variables servant à effectuer les appels systèmes*/
	private Desktop desktop; 					// utilisé pour ouvrir/éditer/imprimer les fichiers
	private FileSystemView fileSystemView;		// fournit les icones et les noms des fichiers
	private File courant; 					// fichier actuel selectionné
	private File liste;
	private JPanel gui; 						// methode principale contenant les graphics user interface
	/*******************************/
	private JTree tree; 						// pour donner l'aborescence des fichiers du systeme (partie gauche de l'explorateur de fichier
	private DefaultTreeModel treeModel; 		// créer un modele de l'arborescence
	private JProgressBar progressBar;
	private FileTableModel fileTableModel; 		// modele de tableau pour les fichiers
	private JTable table; 						// créer un tableau pour disposer les dossiers dans le panel
	private int rowIconPadding = 6; 			// colonnes pour tableau donnant les attributs d'un fichier ou dossier
	/*******************************/
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	

	/* les différents boutons pour agir sur un dossier ou fichier et gerer les évenements */
	private JButton ouvrir;
	private JButton openwith;
	private JButton imprimer;
	private JButton editer;
	private JButton deleteFile;
	private JButton newFile;
	private JButton copyFile;
	private JButton cutFile;
	private JButton pasteFile;
	private JButton renameFile;
	private JButton fenetre;
	private JButton Backward;
	private JButton forward;
	private JButton taille;

	long longueur;
	
	/*******************************/
	private JLabel fileName; 					// label pour le nom du fichier
	private JTextField path; 					// chemin d'accès du fichier ou du dosier selectionner
	private JTextField searchBar;
	private JLabel date;						// label pour la date de création de l'élément sélectionner
	private JLabel size; 						// la taille l'élément sélectionner
	private JLabel Size = new JLabel();
	private JLabel number;
	
	/*******************************/
	private JPanel newP; 				// panel qui s'affiche lorsque l'on souhaite créer un nouveau dossier ou fichier
	private JRadioButton newTypeDirectory;
	private JRadioButton newTypeFile; 			// case à cocher pour choisir de créer soit un fichier soit un dossier
	private JRadioButton lecture;
	private JRadioButton ecriture;
	private JPanel prop;
	/*******************************/
	private Icon ic1 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\icons8-open-file-under-cursor-32.png");
	private Icon ic2 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\icons8-edit-30.png");
	private Icon ic3 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\Capture d’écran 2021-12-19 011624.png");
	private Icon ic4 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\add-file.png");
	private Icon ic5 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\duplicate.png");
	private Icon ic6 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\64873_textfield_rename_icon.png");
	private Icon ic7 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\Capture d’écran 2021-12-19 013249.png");
	private Icon ic8 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\Capture d’écran 2021-12-19 013038.png");
	private Icon ic9 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\Capture d’écran 2021-12-19 012006.png");
	private Icon ic10 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\couper.png");
	private Icon ic12 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\back.png");
	private Icon ic13 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\foward.png");
	private Icon ic14 = new ImageIcon("C:\\Users\\TOSHIBA\\Documents\\Réalisations en JAVA\\SE_fileManager\\openwith.png");
	
	private JTextField name; 					// zone de texte pour saisir le nom du nouveau fichier ou dossier
	private ArrayList <String> chemin = new ArrayList<>();
	String f = new String();
	int b=1;
	int n=0;
	public static final String TITRE = "Ngome's Explorer";
	
/** ICI ON S'ATELE A CREER NOTRE INTERFACE GRAFIQUE GUI. Elle consiste en un conteneur
  dans lequel on place tous nos éléments graphiques en précisant leur position 
  pour qu'il n'y ait pas superposition d'éléments sur d'autres*/
	public Container getGui() {
		if (gui == null) /* s'il n' y a rien dans le conteneur gui */ {
			
			
			gui = new JPanel(new BorderLayout(3, 3)); 					// on affecte au conteneur un panel dont on définit les position des bordures
			gui.setBackground(Color.white);
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));					// vue que c'est un objet de type JPanel, on définit les bordures

			fileSystemView = FileSystemView.getFileSystemView();		// on fait appel au systeme d'exploitation pour avoir les informations mises àdisposition sur les fichiers
			desktop = Desktop.getDesktop();								//la classe Desktop fournit les methodes pour ouvrir,editer,imprimer un fichier, elle fournit l'application par défaut pour effctuer chacune de ses taches

/***********On définit d'abord l'arborescence du système de fichier*************/

			DefaultMutableTreeNode root = new DefaultMutableTreeNode(); // création d'un objet noeud pour un répertoire racine parent de tous les autres dossiers
			treeModel = new DefaultTreeModel(root); 					// definition d'un model pour l'arbre
			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {
					
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					/*on a ajouté un selectionneur dans l'arbre pour changer à chaque clic fois le contenu du tableau et recupérer les informations relatives à la sélection*/
					subNode(node);
					setFileDetails((File) node.getUserObject());
					
					File fileSearch= (File) node.getUserObject();
					searchBar.setText(fileSearch.getPath());
					f = searchBar.getText();
					if(chemin.size()==0) chemin.add(f);
					else if(f!=chemin.get((chemin.size()-1))) chemin.add(f);
					
					number.setText("  ");
					
				}
			};
			
			/*
			 * creation des noeuds à chaque niveau de l'arbre et récuperation des chemins
			 * de chaque parent uniquement lorsque l'on clique sur un élément de l'arbre
			 * c'est à dire que l'on charge les sous noeud à la demande de l'utilisateur pour economiser 
			 * en espace mémoire pour le chargement de l'application
			 */									
				
			
			File[] roots = fileSystemView.getRoots(); 
			
			for (File R : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(R);
				root.add(node);
				
				File[] files = fileSystemView.getFiles(R, true);
				for (File file : files)/* on affiche que les repertoires dans l'arborescence */ {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
						System.out.println(file);
					}
				}
			}
			
			tree = new JTree(treeModel); 								// associe le model créé à l'arborescence, la partie gauche de l'explorateur
			tree.setCellRenderer(new TreeCellRenderer());
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setRootVisible(false);
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);				// la barre de défilement de l'arbre devient indissociable de celle ci
			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);

/***********On définit ensuite un panel qui contient un tableau permettant de visualiser la liste des fichiers****************/			

			JPanel detailView = new JPanel(new BorderLayout(3, 3)); 	// panel principal qui se trouve dans la partie droite de l'explorateur
	
			detailView.setBackground(Color.white);
			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// permet de selectionner une  seule ligne dans le tableau table
			table.setAutoCreateRowSorter(true); 						// methode de la classe JTable qui permet de trier automatiquement les éléments dans un tableau
			table.setShowVerticalLines(false); 							// affiche les lignes verticales pour separer les colonnes dans un tableau
			table.setShowHorizontalLines(false);
			listSelectionListener = new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent lse) {		// pour changer les éléments sélectionnés dans le tableau à chaque click sur un élément différent
					int row = table.getSelectionModel().getLeadSelectionIndex(); 
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));// methode qui permet de recupérer effectivement le fichier selectionné dans le tableau
					
				}
			};
			table.getSelectionModel().addListSelectionListener(listSelectionListener);
			
			JScrollPane tableScroll = new JScrollPane(table);			// Associe les barres de défilement au tableau, ils sont maintenant indissociables
			Dimension d = tableScroll.getPreferredSize();
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight()/2));
			detailView.add(tableScroll, BorderLayout.CENTER); 			// associe les barres de défilement qui est directement associé au tableau au panel

			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent ev) {
					if(ev.getClickCount()==2) {
						try {
							
							if(courant.exists()) {
								if(courant.isFile()) {
									desktop.open(courant);	
								}
								if(courant.isDirectory()) {
								
									liste = new File(courant.getPath());
									liste = courant;
									File [] Files ;
									Files = liste.listFiles();
									setTableData(Files);
									searchBar.setText(liste.getPath());
									f = searchBar.getText();
									if(chemin.size()==0) chemin.add(f);
									else if(f!=chemin.get((chemin.size()-1))) chemin.add(f);	
								}
							}
							
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

			searchBar= new JTextField();
			searchBar.setEditable(true);
			detailView.add(searchBar,BorderLayout.NORTH);
			
/***********On définit par la suite une composition de panels qui contient toutes les informations essentielles relatives à l'élément sélectionné****************/
			/*
			 * ici on compose 3 panels afin de créer un seul label en apparence qui donnera
			 * tous les détails sur un fichier tous les éléments qui nécessitent une valeur
			 * sont ajoutés auValues et ceux qui disposent juste un détail au
			 * Details
			 */
			JPanel Details = new JPanel(new BorderLayout(4, 2));/* panel principal qui contient fileLabels et Values */
			Details.setBorder(new EmptyBorder(0, 6, 0, 6));
			Details.setBackground(Color.white);
			/*******************************/
			JPanel Labels = new JPanel(new GridLayout(0, 1, 2, 2));
			Details.add(Labels, BorderLayout.WEST);
			Labels.setBackground(Color.white);
			/*******************************/
			JPanel Values = new JPanel(new GridLayout(0, 1, 2, 2));
			Details.add(Values, BorderLayout.CENTER);
			Values.setBackground(Color.white);
			/*******************************/
			Labels.add(new JLabel("Nom"));
			fileName = new JLabel();
			Values.add(fileName);
			Labels.add(new JLabel("Chemin d'accès"));
			path = new JTextField(5);
			path.setEditable(true);// on peut ecrire dans cette zone de texte
			path.setBackground(Color.white);
			Values.add(path);
			Labels.add(new JLabel("Modifié le"));
			date = new JLabel();
			Values.add(date);
			Labels.add(new JLabel("Taille"));
			size = new JLabel();
			Values.add(size);
			Labels.add(new JLabel("Nombre d'éléments"));
			number = new JLabel();
			Values.add(number);
  
			detailView.add(Details, BorderLayout.SOUTH);
			
	
/***********On continue dans la même lancée en définissant une barre d'outils contenant les boutons qui nous permettront de définir le actions effectuables sur les fichiers ****************/
			JToolBar toolBar = new JToolBar(); 
			toolBar.setFloatable(true); // la barre d'outils ne saurait être déplacée
			/*******************************/
			Backward = new JButton("précédent",ic12);
			Backward.setVerticalTextPosition(SwingConstants.BOTTOM);
			Backward.setHorizontalTextPosition(SwingConstants.CENTER);

			Backward.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File(chemin.get(chemin.size()-1-b));
					b=chemin.size()-1-b;
					File fil [] = file.listFiles();
				
					setTableData(fil);
					searchBar.setText(file.getPath());
					b+=1;
					}
				
			});
			
			toolBar.add(Backward);
			/*******************************/
			forward = new JButton("suivant",ic13);
			forward.setVerticalTextPosition(SwingConstants.BOTTOM);
			forward.setHorizontalTextPosition(SwingConstants.CENTER);
			forward.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File(chemin.get(n+1));
					n=chemin.size()-1-b;
					File fil [] = file.listFiles();
					searchBar.setText(file.getPath());
					//((DefaultTableModel)table.getModel()).setRowCount(0);
					
					setTableData(fil);
					searchBar.setText(file.getPath());
					n+=1;
					b-=1;
				}
				
			});
			toolBar.add(forward);
			/*******************************/
			ouvrir = new JButton("Ouvrir",ic1);
			ouvrir.setVerticalTextPosition(SwingConstants.BOTTOM);
			ouvrir.setHorizontalTextPosition(SwingConstants.CENTER);
			ouvrir.addActionListener(// on attribue au bouton open une action spécifique d'ouveture lorsqu'on cliquera sur celui ci sera
					new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							try {
								if(courant.exists()) {
									
									if(courant.isFile()) {
										desktop.open(courant);
										
									}
									if(courant.isDirectory()) {
										liste = new File(courant.getPath());
										liste = courant;
										File [] Files ;
										Files = liste.listFiles();
										setTableData(Files);
										searchBar.setText(liste.getPath());
									}
								}						
															// d'une application système par défaut
							} catch (Throwable t) /* surper classe de toutes les erreurs qui va permettre de catch */ {
								showThrowable(t);/* affiche le cas d'erreur soulevé */
							}
							gui.repaint();
						}
					});
			toolBar.add(ouvrir); // on ajoute à la barre d'outils le bouton openfile
			/*******************************/
			openwith = new JButton("ouvrir avec",ic14);
			openwith.setVerticalTextPosition(SwingConstants.BOTTOM);
			openwith.setHorizontalTextPosition(SwingConstants.CENTER);
			openwith.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if(courant.exists()) {
							if(courant.isFile()) {
								File withoutExt= new File(courant.getParentFile(), FilenameUtils.removeExtension(courant.getName()));
								
								courant.renameTo(new File(courant.getParentFile(), FilenameUtils.removeExtension(courant.getName())));//withoutExt
								System.out.println(courant.getName());
								desktop.open(withoutExt);
								
								withoutExt.renameTo(new File(courant.getParentFile(), courant.getName()));
							}
							
						}						
													// d'une application système par défaut
					} catch (Throwable t) /* surper classe de toutes les erreurs qui va permettre de catch */ {
						showThrowable(t);/* affiche le cas d'erreur soulevé */
					
				}
				}
			});
			toolBar.add(openwith);
	
			/*******************************/
			editer = new JButton("Editer",ic2);
			editer.setVerticalTextPosition(SwingConstants.BOTTOM);
			editer.setHorizontalTextPosition(SwingConstants.CENTER);
			editer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						desktop.edit(courant);
					} catch (Throwable t) {
						showThrowable(t);
					}
				}
			});
			toolBar.add(editer);
			/*******************************/
			imprimer = new JButton("Imprimer",ic3);
			imprimer.setVerticalTextPosition(SwingConstants.BOTTOM);
			imprimer.setHorizontalTextPosition(SwingConstants.CENTER);
			imprimer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						desktop.print(courant);
					} catch (Throwable t) {
						showThrowable(t);
					}
				}
			});
			toolBar.add(imprimer);
			/*******************************/
			// demande au système l'autorisation aux boutons d'effectuer les actions d'ouvrir,editer et imprimer
			//ouvrir.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
			
			editer.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
			imprimer.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

			toolBar.addSeparator();
			/*******************************/
			newFile = new JButton("Nouveau",ic4);
			newFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			newFile.setHorizontalTextPosition(SwingConstants.CENTER);
			newFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					newFile();
				}
			});
			toolBar.add(newFile);
			/*******************************/
			copyFile = new JButton("Copier",ic5);
			copyFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			copyFile.setHorizontalTextPosition(SwingConstants.CENTER);
			
			pasteFile = new JButton("coller/copier",ic9);
			pasteFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			pasteFile.setHorizontalTextPosition(SwingConstants.CENTER);
			copyFile.addActionListener( new ActionListener()  {
				  public void actionPerformed(ActionEvent ae) {
				
				String from =courant.getPath();
				String nom = courant.getName();
				 File src = new File(from);
				
				  
				  pasteFile.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String to = courant.getParent()+"\\"+nom;
						
						
						java.nio.file.FileSystem fs = FileSystems.getDefault();
						if (courant.isFile()) {
							try {
								if(ouvert(src)) {
									Files.copy(fs.getPath(from) ,fs.getPath(to), StandardCopyOption.REPLACE_EXISTING);
									TreePath parentPath = findTreePath(courant.getParentFile());
									DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
									subNode(parentNode);
									setFileDetails(courant.getParentFile());
									JOptionPane.showMessageDialog(gui, "opération réussie");
									
								}
								else {
									showErrorMessage("fermer le fichier et essayer à nouveau", "Echec de l'Opération");
								}

							}
							catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							}
								
						}else {
							try {
								
								FileUtils.copyDirectoryToDirectory(src, courant);
								
								TreePath parentPath = findTreePath(courant);
								DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
								DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(courant);
								
								subNode(parentNode);
								treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}	  
				  });
				  }	
			 });
			  toolBar.add(copyFile);
			  
			  toolBar.add(pasteFile);
			  
			/*******************************/
			  cutFile = new JButton("couper",ic10);
			  cutFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			  cutFile.setHorizontalTextPosition(SwingConstants.CENTER);
			  
			  cutFile.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String from =courant.getPath();
					String nom = courant.getName();
					File src = new File(from);
	
					pasteFile.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							String to = courant.getParent()+"\\"+nom;
												
							java.nio.file.FileSystem fs = FileSystems.getDefault();
							if (courant.isFile()) {
								try {
									if(ouvert(src)) {
										Files.copy(fs.getPath(from) ,fs.getPath(to), StandardCopyOption.REPLACE_EXISTING);
										TreePath parentPath = findTreePath(courant.getParentFile());
										DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
										subNode(parentNode);
										setFileDetails(courant.getParentFile());
										JOptionPane.showMessageDialog(gui, "opération réussie");
									}
									else {
										showErrorMessage("fermer le fichier et essayer à nouveau", "Echec de l'Opération");
									}
									
									
								}
								catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								}
								src.delete();	
							}
							else {
								try {
									TreePath parentPath = findTreePath(courant);
									
									FileUtils.copyDirectoryToDirectory(src, courant);
									
									DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
									DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(courant);
									
									treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
									subNode(parentNode);	
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							try {
							FileUtils.deleteDirectory(src);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 	
							}
						}	  
					  });		
				}	
				
				  
			  });
			  toolBar.add(cutFile);

			/*******************************/
			renameFile = new JButton("Renommer",ic6);
			renameFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			renameFile.setHorizontalTextPosition(SwingConstants.CENTER);
			
			renameFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					renameFile();
				}
			});
			toolBar.add(renameFile);
			/*******************************/
			deleteFile = new JButton("Supprimer",ic7);
			deleteFile.setVerticalTextPosition(SwingConstants.BOTTOM);
			deleteFile.setHorizontalTextPosition(SwingConstants.CENTER);
			
			deleteFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					 if(ouvert(courant)) {
						  delete();
					 }else {
						showErrorMessage("suppression impossible", "Echec de l'Opération");
					 }
				}
			});
			toolBar.add(deleteFile);
			/*******************************/
			
			toolBar.addSeparator();
			/******************************/
			fenetre = new JButton("Nouvelle fenetre",ic8);
			fenetre.setVerticalTextPosition(SwingConstants.BOTTOM);
			fenetre.setHorizontalTextPosition(SwingConstants.CENTER);
			
			fenetre.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								
								UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
							} catch (Exception weTried) {
							}
							JFrame nf = new JFrame(TITRE);
							nf.setResizable(true);
							nf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

							ProjetSE Explorateur1 = new ProjetSE();
							nf.setContentPane(Explorateur1.getGui());
							nf.pack();
							nf.setLocationByPlatform(true);
							nf.setMinimumSize(nf.getSize());
							nf.setVisible(true);

							
						}
					});
				}	
			});
			toolBar.add(fenetre);
			toolBar.addSeparator();
			/*************************/
			taille = new JButton("Taille dossier") ;
			taille.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					propriete();
					
				}

			});
			toolBar.add(taille);
			lecture= new JRadioButton("Droit de lecture");
			ecriture = new JRadioButton("Droit d'écriture");
			
			
			toolBar.add(lecture);
			toolBar.add(ecriture);
			
			
		
			JPanel outils = new JPanel(new BorderLayout(3, 3));
			outils.add(toolBar);
			gui.add(outils, BorderLayout.PAGE_START); 
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);
			gui.add(splitPane, BorderLayout.CENTER);
			//On ajoute un progressbar de la droite vers la gauche pour définir le sens de display de l'arbre vers le detail view c'est à dire de la gauche vers la droite
			JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
			simpleOutput.setBackground(Color.white);
			progressBar = new JProgressBar();
			simpleOutput.add(progressBar, BorderLayout.EAST);
			progressBar.setVisible(false);
			gui.add(simpleOutput, BorderLayout.SOUTH);
			  
		}
		return gui;
	}
/** ICI ON S'ATELE A IMPLEMENTER NOS DIFFERENTES METHODES DEFINIES 
	* POUR LES EVENEMENTS DES BOUTONS DE L'INTERFACE GRAPHIQUE.*/	
	
	/* findTreePath va se charger de trouver les chemins d'accès au noeud d'un fichier à partir dans l'arbre, ceux ci vont être utlisés dans les methodes */
	
	private TreePath findTreePath(File file) {
		for (int j = 0; j < tree.getRowCount(); j++) {
			TreePath treePath = tree.getPathForRow(j);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile.equals(file)) {
				return treePath;
			}
		}
		
		return null;
	}
/************On définit la méthode pour afficher les propriétés d'un fichier ou dossier*******************/
	private void propriete() {
		if (courant == null) {
			showErrorMessage("Pas d'élément selectionné pour renommer.", "Selectionner un élément");
			return;
		}
		if(prop ==null ) {
			prop = new JPanel(new BorderLayout(10, 10));
			Size = new JLabel();
			longueur = folderSize(courant);
			Size.setText(longueur+" Ko");
			prop.add(Size);
			JOptionPane.showConfirmDialog(gui,prop,"Taille du dossier",JOptionPane.OK_OPTION);	
		}
		else {
			prop = new JPanel(new BorderLayout(10, 10));
			Size = new JLabel();
			longueur = folderSize(courant);
			Size.setText(longueur+" Ko");
			prop.add(Size);
			JOptionPane.showConfirmDialog(gui,prop,"Taille du dossier",JOptionPane.OK_OPTION);	
		}
		
		
		
	}
	
/************On définit la méthode pour renommer un fichier ou dossier*******************/
	private void renameFile() {
		if (courant == null) {
			showErrorMessage("Pas d'élément selectionné pour renommer.", "Selectionner un élément");
			return;
		}

		String NewName = JOptionPane.showInputDialog(gui, "Nouveau nom");//génère directement une boite de dialogue munie d'une zone de texte dont la 
		if (NewName != null) {
			/*on vérifie qu'il y a du texte écrit dans la boite de dialogue*/
			try {

				TreePath parentPath = findTreePath(courant.getParentFile());
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
			

				boolean renamed = courant.renameTo(new File(courant.getParentFile(), NewName));
				File creer = new File(courant.getParent()+"\\"+NewName);
				if (renamed) {
					if (courant.isDirectory()) {
					

						// on retire le noeud courant
						TreePath currentPath = findTreePath(courant);
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
						
						treeModel.removeNodeFromParent(currentNode);

						// on ajoute le nouveau noeud
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(creer);
						
						treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
					}
					subNode(parentNode);
					courant=creer;
					setFileDetails(courant);
				} else {
					String msg = "L'élément '" + courant + "' ne peut être renommer.";
					showErrorMessage(msg, "Echec de l'opération");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
		
	}
/************On définit la méthode pour supprimer un fichier ou dossier*******************/
	private void delete() {
		if (courant == null) {
			showErrorMessage("Pas d'élément selectionné pour suppression.", "Selectionner un élément");
			return;
		}

		int result = JOptionPane.showConfirmDialog(gui, "confirmer sa suppression définitive", "Suppression élément",JOptionPane.ERROR_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
		//	boolean directory = courant.isDirectory();
            TreePath parentPath = findTreePath(courant.getParentFile());
            DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode) parentPath.getLastPathComponent();

			if (courant.isFile()) {
				if(courant.delete()) {
					JOptionPane.showMessageDialog(gui, "supression réussie");
					courant=courant.getParentFile();
					setFileDetails(courant);
				}
			}
			else {
				try {
					TreePath currentPath = findTreePath(courant);
					FileUtils.deleteDirectory(courant);
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();

					treeModel.removeNodeFromParent(currentNode);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				courant=courant.getParentFile();
				setFileDetails(courant);
			}
			
			subNode(parentNode);
			}
		
		gui.repaint();
	}
	/*************************/
	public boolean ouvert(File file) {
		boolean oui = false;
		File retour = new File(file.getPath());
		retour=file;
		try  {
		FileOutputStream stream = new FileOutputStream(retour.getPath());
		FileChannel channel = stream.getChannel();
		FileLock lock = channel.tryLock();
		if(lock != null) {
			System.out.println("fichier non utilisé donc fermé");
			oui= true;
			lock.release();	
		}
		else {
			System.out.println("fichier est utilisé donc ouvert");
			oui= false;
		}
		channel.close();
		stream.close();
		}catch (Throwable t) {
			showThrowable(t);
		}
		return oui;
	}
/************On définit la méthode pour créer un fichier ou dossier*******************/
	private void newFile() {
		if (courant == null) {
			showErrorMessage("Pas d'emplacement pour le nouvel élément.", "Selectionner un emplacement");
			return;
		}
		/*on designe la boite de dialogue dans laquelle on va réaliser les actions de création de fichier*/
		if (newP == null) {
			newP = new JPanel(new BorderLayout(3, 3));

			JPanel southRadio = new JPanel(new GridLayout(1, 0, 2, 2));
			newTypeFile = new JRadioButton("Fichier", true);
			newTypeDirectory = new JRadioButton("Répertoire");
			ButtonGroup bg = new ButtonGroup();
			bg.add(newTypeFile);
			bg.add(newTypeDirectory);
			southRadio.add(newTypeFile);
			southRadio.add(newTypeDirectory);

			name = new JTextField(15);

			newP.add(new JLabel("Nom"), BorderLayout.WEST);
			newP.add(name);
			newP.add(southRadio, BorderLayout.SOUTH);
		}
		/*prend la valeur zero si on choisi ok et 1 sinon*/
		int result = JOptionPane.showConfirmDialog(gui, newP, "Nom de l'élément.extension", JOptionPane.OK_CANCEL_OPTION);
		/*on crée vraiment le nouveau fichier ou repertoire*/
		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean created;
				File parentFile = courant;
				if (!parentFile.isDirectory()) {
					parentFile = parentFile.getParentFile();
				}
				File file = new File(parentFile, name.getText());
				if (newTypeFile.isSelected()) {
					created = file.createNewFile();
				} else {
					created = file.mkdir();
				}
				if (created) {

					TreePath parentPath = findTreePath(parentFile);
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

					if (file.isDirectory()) {
						// add the new node..
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

						
						treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount()); // ajouter un nouveau noeud apres creation
					}

					subNode(parentNode);
				} else {
					String msg = "L'élément '" + file + "ne peut être créé.";
					showErrorMessage(msg, "Echec de l'Opération");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}
/************On définit la méthode pour copier un fichier ou dossier*******************/

/****ICI ON DEFINIT LES EVENEMENTS SUR LES ELEMENTS GRAPHIQUES*****/
  /*Méthode pour définir la largeur des colonnes d'un tableau*/
	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}
/************On définit la méthode pour remplir le tableau à partir d'une sélection  dans l'arbre*******************/
	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// on ajuste la taille de l'icone afin qu'elle puisse suffir dans une ligne du tableau
					table.setRowHeight(icon.getIconHeight() + rowIconPadding);

					setColumnWidth(0, -1);
					//setColumnWidth(3, 60);
					table.getColumnModel().getColumn(3).setMaxWidth(100);
					cellSizesSet = true;
				}
			}
		});
	}
/************On définit la méthode pour afficher les informations spécifiques à un fichier et à la barre d'état de l'explorateur*******************/
	private void setFileDetails(File file) {
		courant = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		
		if (file.isFile()) {
			if (file.length()==0) size.setText(file.length() + " Ko");
			else size.setText((file.length()/1024)+1 + " Ko");
		}
		else {
			size.setText(" ");
		}
		searchBar.setText(file.getParent());
		number.setText("  "+table.getRowCount()+"  éléments");
		lecture.setSelected(file.canRead());
		lecture.setEnabled(false);
		ecriture.setSelected(file.canWrite());
		ecriture.setEnabled(false);

		JFrame f = (JFrame) gui.getTopLevelAncestor();
		if (f != null) {
			if (file.isDirectory()) {
				f.setTitle(TITRE + " / " + fileSystemView.getSystemDisplayName(file));
			}	
		}

		gui.repaint();
	}
	
	  public  long folderSize(File file) { 
		  long length =0; 
		  for (File file1 :file.listFiles()) {
			  if (file1.isFile()) length += file1.length();
			  else length += folderSize(file1);
			   }
		  if (length==0) return length;
		  else return (length/1024)+1;
		  
		  }
	

/************On définit la méthode pour afficher les sous repertoires dans l'arbre*******************/		
	private void subNode(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);                                                                                                                                                                                                                                                                          

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); 
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

		
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

		
			protected void done() {
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();// ajoute le thread à la liste d'execution du thread d'arrière plan
	}
/************On définit la méthode pour afficher une erreur qui survient lors de l'execution d'une méthode*******************/		
	/*methode qui affiche les différentes erreurs que nous pouvont rencontrer tout au long des executions d'une methode*/
	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}
	/*ici on ejecte toutes les exceptions soulevées dans les try catch*/
	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}
	
/** ICI NOUS SOMMES DANS LE POINT D'ENTREE DE NOTRE PROGRAMME*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame f = new JFrame(TITRE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				ProjetSE Explorateur = new ProjetSE();
				f.setContentPane(Explorateur.getGui());

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				
			}
		});
	}
/****fin de la classe pricipale qui permet d'instancier uniquement un objet de type ProjetSE à l'instar de explorateur****/
}
/**ICI ON DEFINIT DES CLASSES SUPPLEMENTAIRES POUR INSTANCIER DES OBJETS DEFINITS DANS LA CLASSE ProjetSE*/
class FileTableModel extends AbstractTableModel {

	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = { "Vue", "Nom", "Modifié le", "Taille", };

	FileTableModel() {
		this(new File[0]);
	}

	FileTableModel(File[] files) {
		this.files = files;
	}

	public Object getValueAt(int row, int column) { // renvoie l'élément présent dans la cellule indexée par son numéro de ligne et son numéro de colonne 
		File file = files[row];
			
		switch (column) {
		case 0:
			return fileSystemView.getSystemIcon(file);
		case 1:
			
			return fileSystemView.getSystemDisplayName(file);
		case 2:
			return file.lastModified();
		case 3:
			if (file.isFile()) {
				if (file.length()==0) return 0; 
				else return (file.length()/1024)+1 +" Ko";
			}
			
		}
		return "";
	}

	

	public int getColumnCount() { //détermine pour un model le nombre de colonnes à afficher
		return columns.length;
	}

	public Class<?> getColumnClass(int column) {//sert à spécifier la classe des objets relativement à une colonne
		switch (column) {
		case 0:
			return ImageIcon.class;
		
		case 2:
			return Date.class;
		
		case 3:
			return String.class;
		
		
		}
		return String.class;
	}

	public String getColumnName(int column) { // sert à renvoyer les nom de chaque colonne
		return columns[column];
	}

	public int getRowCount() {
		return files.length;
	}

	public File getFile(int row) {
		return files[row];
	}

	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();// notifie toutes les vues de modification
	}
}

/********CLASSE UTILISEE POUR DEFINIR L'ASPECT DES NOEUDS************/
class TreeCellRenderer extends DefaultTreeCellRenderer {

	private FileSystemView fileSystemView;

	private JLabel label;

	TreeCellRenderer() {
		label = new JLabel();
		label.setOpaque(true);
		fileSystemView = FileSystemView.getFileSystemView();
	}
 
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();
		label.setIcon(fileSystemView.getSystemIcon(file));
		label.setText(fileSystemView.getSystemDisplayName(file));
		label.setToolTipText(file.getPath());

		if (selected) {
			label.setBackground(backgroundSelectionColor);
			label.setForeground(textSelectionColor);
		} else {
			label.setBackground(backgroundNonSelectionColor);
			label.setForeground(textNonSelectionColor);
		}

		return label;
	}
}

