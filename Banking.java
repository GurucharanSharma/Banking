import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

class BankingFrame extends JFrame implements ListSelectionListener, ActionListener, FocusListener, ItemListener
{
	Connection con;
	BankingPanel detailPanelCT, detailPanelStat;
	BankingPanel bp[];
	JCheckBox jcb[];
	JTabbedPane jtp;
	JTextField aitext, nametext, balancetext, dojtext;
	JTextField aitextRA, nametextRA, balancetextRA, dojtextRA;
	JTextField transidtextT, aitextT, nametextT, balancetextT, amttextT;
	JTextField nametextCT, baltextCT, resulttextCT;
	JTextField aitextStat;
	JButton s1, s2, s3, s4, s5, c1, c2, c3, c5;
	JRadioButton jrbT1, jrbT2;
	ButtonGroup br;
	PreparedStatement pstAID, pst1, pst2, pst2_1, pst2_2, pst3, pst3_1, pst3_2, pst4, pst4_1, pst4_2, pst4_3, pst5;
	JList list, listCT;
	Vector<String> vct = new Vector<String>();
	Vector<String> tid;
	int lastId, lastTId, id, t_id;
	boolean lock = false, lockCT = false;
	String date;
	
	BankingFrame(Connection con)
	{
		super("Banking Application");
		setSize(600, 600);

        jtp = new JTabbedPane();
		jtp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		jtp.setTabPlacement(JTabbedPane.TOP);

		this.con = con;
		Statement st;
		ResultSet rs;
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		java.util.Date dt    = new java.util.Date();
		String str[] = {"New Account", "Retire Account", "Transaction", "Cancel Transaction", "Statement"};
		
		try
		{
			pstAID = con.prepareStatement("SELECT max(accnt_id) AS maxId FROM Customer");
			pst1   = con.prepareStatement("INSERT INTO Customer VALUES (?, ?, ?, ?);");
			pst2   = con.prepareStatement("DELETE FROM Customer WHERE accnt_id = ?");
			pst2_1 = con.prepareStatement("SELECT * FROM Customer");
			pst2_2 = con.prepareStatement("SELECT * FROM Customer WHERE accnt_id = ?");
			pst3   = con.prepareStatement("INSERT INTO Transaction VALUES(?, ?, ?, ?, ?)");
			pst3_1 = con.prepareStatement("SELECT * FROM Transaction WHERE trans_id = ?");
			pst3_2 = con.prepareStatement("UPDATE Customer SET balance = ? WHERE accnt_id = ?");
			pst4   = con.prepareStatement("SELECT * FROM Transaction WHERE accnt_id = ?");
			pst4_1 = con.prepareStatement("DELETE FROM Transaction WHERE accnt_id = ?");
			pst4_2 = con.prepareStatement("DELETE FROM Transaction WHERE trans_id = ?");
			pst4_3 = con.prepareStatement("UPDATE Customer SET balance = ? where accnt_id = ?");
			
			// making the list for retire account
			rs = pst2_1.executeQuery();
			while (rs.next())
			{
				String id = String.format("%d", rs.getInt("accnt_id"));
				vct.add(id);
			}
			
			// New Account
			rs = pstAID.executeQuery();
			rs.next();
			lastId = rs.getInt("maxId");
			date   = sdf.format(dt);
			if (lastId == 0)
				lastId = 101;
			else
				lastId++;
			
			// Transaction
			st = con.createStatement();
			rs = st.executeQuery("SELECT max(trans_id) AS maxTId FROM Transaction");
			rs.next();
			lastTId = rs.getInt("maxTId");
			if (lastTId == 0)
				lastTId = 1;
			else
				lastTId++;
		}
		catch (SQLException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
		
		// <---------- New Account --------------------------------------------------> //
		
		BankingPanel bp1        = new BankingPanel(15, 80, 15, 80);
		BankingPanel textPanel1 = new BankingPanel( 5, 10,  5, 10);
		BankingPanel lblPanel   = new BankingPanel(15, 10, 15, 10);
		BankingPanel fldPanel   = new BankingPanel(15, 10, 15, 10);
		BankingPanel btnPanel1  = new BankingPanel(15, 30, 15, 30);
		bp1.setLayout(new BorderLayout(10, 10));
		textPanel1.setLayout(new BorderLayout());
		lblPanel.setLayout(new GridLayout(4, 1, 5, 40));
		fldPanel.setLayout(new GridLayout(4, 1, 5, 40));
		btnPanel1.setLayout(new GridLayout(1, 2, 30,10));
		textPanel1.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		btnPanel1.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		
		s1 = new JButton("Submit");
		c1 = new JButton("Clear");
		s1.setFont(new Font("lucida console", Font.BOLD, 16));
		c1.setFont(new Font("lucida console", Font.BOLD, 16));
		
		JLabel ai      = new JLabel("  Account Id");
		JLabel name    = new JLabel("        Name");
		JLabel balance = new JLabel("     Balance");
		JLabel doj     = new JLabel("Joining Date");
		ai.setFont(new Font("lucida console", Font.PLAIN, 15));
		name.setFont(new Font("lucida console", Font.PLAIN, 15));
		balance.setFont(new Font("lucida console", Font.PLAIN, 15));
		doj.setFont(new Font("lucida console", Font.PLAIN, 15));
		
		aitext      = new JTextField(String.valueOf(lastId));
		nametext    = new JTextField();
		balancetext = new JTextField();
		dojtext     = new JTextField(date);
		aitext.setEditable(false);
		dojtext.setEditable(false);
		aitext.setFont(new Font("lucida console", Font.BOLD, 15));
		nametext.setFont(new Font("lucida console", Font.PLAIN, 15));
		balancetext.setFont(new Font("lucida console", Font.PLAIN, 15));
		dojtext.setFont(new Font("lucida console", Font.BOLD, 15));
		
		lblPanel.add(ai);
		lblPanel.add(name);
		lblPanel.add(balance);
		lblPanel.add(doj);
		fldPanel.add(aitext);
		fldPanel.add(nametext);
		fldPanel.add(balancetext);
		fldPanel.add(dojtext);
		btnPanel1.add(s1);
		btnPanel1.add(c1);
		textPanel1.add(lblPanel, BorderLayout.WEST);
		textPanel1.add(fldPanel, BorderLayout.CENTER);
		bp1.add(textPanel1, BorderLayout.CENTER);
		bp1.add(btnPanel1, BorderLayout.SOUTH);
		jtp.add(str[0],bp1);
		
		s1.addActionListener(this);
		c1.addActionListener(this);
		
		// <-------------------------------------------------------------------------> //
		
	
		// <---------- Retire Account -----------------------------------------------> //

		BankingPanel bpRA          = new BankingPanel(15, 80, 15, 80);
		BankingPanel listPanelRA   = new BankingPanel( 5, 10,  5, 10);
		BankingPanel raPanel       = new BankingPanel(15, 10, 15, 10);
		BankingPanel btnPanelRA    = new BankingPanel( 5, 30,  5, 30);
		BankingPanel detailPanelRA = new BankingPanel(15, 10, 15, 10);
		BankingPanel lblPanelRA    = new BankingPanel( 5, 10,  5, 10);
		BankingPanel fieldPanelRA  = new BankingPanel( 5, 10,  5, 10);
		bpRA.setLayout(new BorderLayout(5, 5));
		listPanelRA.setLayout(new GridLayout(1, 1, 10, 10));
		raPanel.setLayout(new BorderLayout());
		btnPanelRA.setLayout(new GridLayout(1, 2, 50, 10));
		detailPanelRA.setLayout(new BorderLayout());
		lblPanelRA.setLayout(new GridLayout(4, 1, 5, 40));
		fieldPanelRA.setLayout(new GridLayout(4, 1, 5, 40));
		listPanelRA.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		raPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));

		JLabel aiRA      = new JLabel("  Account Id");
		JLabel nameRA    = new JLabel("        Name");
		JLabel balanceRA = new JLabel("     Balance");
		JLabel dojRA     = new JLabel("Joining Date");
		aiRA.setFont(new Font("lucida console", Font.PLAIN, 15));
		nameRA.setFont(new Font("lucida console", Font.PLAIN, 15));
		balanceRA.setFont(new Font("lucida console", Font.PLAIN, 15));
		dojRA.setFont(new Font("lucida console", Font.PLAIN, 15));
		
		aitextRA      = new JTextField();
		nametextRA    = new JTextField();
		balancetextRA = new JTextField();
		dojtextRA     = new JTextField();
		aitextRA.setEditable(false);
		nametextRA.setEditable(false);
		balancetextRA.setEditable(false);
		dojtextRA.setEditable(false);
		aitextRA.setFont(new Font("lucida console", Font.BOLD, 15));
		nametextRA.setFont(new Font("lucida console", Font.BOLD, 15));
		balancetextRA.setFont(new Font("lucida console", Font.BOLD, 15));
		dojtextRA.setFont(new Font("lucida console", Font.BOLD, 15));
		
		s2 = new JButton("Drop");
		c2 = new JButton("Clear");
		s2.setFont(new Font("lucida console", Font.BOLD, 16));
		c2.setFont(new Font("lucida console", Font.BOLD, 16));
		
		list = new JList();
		list.setFont(new Font("lucida console", Font.PLAIN, 16));
		JScrollPane jsp = new JScrollPane(list);
		list.setListData(vct);
		
		lblPanelRA.add(aiRA);
		lblPanelRA.add(nameRA);
		lblPanelRA.add(balanceRA);
		lblPanelRA.add(dojRA);
		fieldPanelRA.add(aitextRA);
		fieldPanelRA.add(nametextRA);
		fieldPanelRA.add(balancetextRA);
		fieldPanelRA.add(dojtextRA);
		listPanelRA.add(jsp);
		detailPanelRA.add(lblPanelRA, BorderLayout.WEST);
		detailPanelRA.add(fieldPanelRA, BorderLayout.CENTER);
		btnPanelRA.add(s2);
		btnPanelRA.add(c2);
		raPanel.add(btnPanelRA, BorderLayout.SOUTH);
		raPanel.add(detailPanelRA, BorderLayout.CENTER);
		bpRA.add(listPanelRA, BorderLayout.WEST);
		bpRA.add(raPanel, BorderLayout.CENTER);
		jtp.add(str[1],bpRA);
		
		s2.addActionListener(this);
		c2.addActionListener(this);
		list.addListSelectionListener(this);
		
		// <-------------------------------------------------------------------------> //
		
		
		// <---------- Transaction --------------------------------------------------> //
		
		BankingPanel bp3         = new BankingPanel(15, 80, 15, 80);
		BankingPanel TransPanel  = new BankingPanel( 5, 10,  5, 10);
		BankingPanel btnPanelT   = new BankingPanel(15, 30, 15, 30);
		BankingPanel lblPanelT   = new BankingPanel(5, 10,  5, 10);
		BankingPanel fldPanelT   = new BankingPanel(5, 10,  5, 10);
		BankingPanel radioPanelT = new BankingPanel( 5, 50, 5, 50);
		BankingPanel textPanelT  = new BankingPanel(5, 10,  0, 10);
		bp3.setLayout(new BorderLayout(10, 10));
		TransPanel.setLayout(new BorderLayout(0, 0));
		btnPanelT.setLayout(new GridLayout(1, 2, 30,10));
		lblPanelT.setLayout(new GridLayout(5, 1, 5, 10));
		fldPanelT.setLayout(new GridLayout(5, 1, 5, 10));
		radioPanelT.setLayout(new BorderLayout(10, 10));
		textPanelT.setLayout(new BorderLayout(10, 10));
		TransPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		btnPanelT.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		
		jrbT1 = new JRadioButton("Credit");
		jrbT2 = new JRadioButton("Debit");
		br = new ButtonGroup();
		br.add(jrbT1);
		br.add(jrbT2);
		jrbT1.setFont(new Font("lucida console", Font.PLAIN, 15));
		jrbT2.setFont(new Font("lucida console", Font.PLAIN, 15));
		
		JLabel trasidT = new JLabel("Trnsaction Id");
		JLabel aiT     = new JLabel("   Account Id");
		JLabel nameT   = new JLabel("         Name");
		JLabel balT    = new JLabel("      Balance");
		JLabel amtT    = new JLabel("       Amount");
		trasidT.setFont(new Font("lucida console", Font.PLAIN, 15));
		aiT.setFont(new Font("lucida console", Font.PLAIN, 15));
		nameT.setFont(new Font("lucida console", Font.PLAIN, 15));
		balT.setFont(new Font("lucida console", Font.PLAIN, 15));
		amtT.setFont(new Font("lucida console", Font.PLAIN, 15));
		
		transidtextT = new JTextField(String.valueOf(lastTId));
		aitextT      = new JTextField();
		nametextT    = new JTextField();
		balancetextT = new JTextField();
		amttextT     = new JTextField();
		transidtextT.setEditable(false);
		nametextT.setEditable(false);
		balancetextT.setEditable(false);
		transidtextT.setFont(new Font("lucida console", Font.BOLD, 15));
		aitextT.setFont(new Font("lucida console", Font.BOLD, 15));
		nametextT.setFont(new Font("lucida console", Font.BOLD, 15));
		balancetextT.setFont(new Font("lucida console", Font.BOLD, 15));
		amttextT.setFont(new Font("lucida console", Font.BOLD, 15));
		
		s3 = new JButton("Submit");
		c3 = new JButton("Clear");
		s3.setFont(new Font("lucida console", Font.BOLD, 16));
		c3.setFont(new Font("lucida console", Font.BOLD, 16));
		
		radioPanelT.add(jrbT1, BorderLayout.WEST);
		radioPanelT.add(jrbT2, BorderLayout.EAST);
		lblPanelT.add(trasidT);
		lblPanelT.add(aiT);
		lblPanelT.add(nameT);
		lblPanelT.add(balT);
		lblPanelT.add(amtT);
		fldPanelT.add(transidtextT);
		fldPanelT.add(aitextT);
		fldPanelT.add(nametextT);
		fldPanelT.add(balancetextT);
		fldPanelT.add(amttextT);
		textPanelT.add(lblPanelT,BorderLayout.WEST);
		textPanelT.add(fldPanelT,BorderLayout.CENTER);
		btnPanelT.add(s3);
		btnPanelT.add(c3);
		TransPanel.add(textPanelT, BorderLayout.CENTER);
		TransPanel.add(radioPanelT, BorderLayout.SOUTH);
		bp3.add(TransPanel, BorderLayout.CENTER);
		bp3.add(btnPanelT, BorderLayout.SOUTH);
		jtp.add(str[2],bp3);
		s3.addActionListener(this);
		c3.addActionListener(this);
		aitextT.addFocusListener(this);
		
		// <-------------------------------------------------------------------------> //
		
		
		// <---------- Cancel Transaction -------------------------------------------> //
		
		BankingPanel bp4          = new BankingPanel(15, 80, 15, 80);
		BankingPanel listPanelCT  = new BankingPanel( 5, 10,  5, 10);
		BankingPanel ctPanelCT    = new BankingPanel( 5, 10,  5, 10);
		BankingPanel textPanelCT  = new BankingPanel( 0,  0,  0,  0);
		BankingPanel lblPanelCT   = new BankingPanel( 5,  0,  0,  0);
		BankingPanel fldPanelCT   = new BankingPanel( 5,  0,  0,  0);
		detailPanelCT             = new BankingPanel( 5,  0,  0,  0);
		BankingPanel btnPanelCT   = new BankingPanel( 5,  0,  10,  0);
		bp4.setLayout(new BorderLayout(10, 10));
		listPanelCT.setLayout(new GridLayout(1, 1));
		ctPanelCT.setLayout(new BorderLayout(10, 10));
		textPanelCT.setLayout(new BorderLayout(10, 10));
		lblPanelCT.setLayout(new GridLayout(2, 1, 5, 5));
		fldPanelCT.setLayout(new GridLayout(2, 1, 5, 5));
		detailPanelCT.setLayout(new GridLayout(15, 1));
		btnPanelCT.setLayout(new BorderLayout(5, 5));
		listPanelCT.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		ctPanelCT.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		
		JLabel nameCT = new JLabel("   Name");
		JLabel balCT  = new JLabel("Balance");
		nameCT.setFont(new Font("lucida console", Font.PLAIN, 15));
		balCT.setFont(new Font("lucida console", Font.PLAIN, 15));
		
		nametextCT = new JTextField();
		baltextCT  = new JTextField();
		nametextCT.setFont(new Font("lucida console", Font.BOLD, 15));
		baltextCT.setFont(new Font("lucida console", Font.BOLD, 15));
		nametextCT.setEditable(false);
		baltextCT.setEditable(false);
		//detailListCT = new JList();
		//detailListCT.setFont(new Font("lucida console", Font.PLAIN, 16));
		JScrollPane jspCT = new JScrollPane(detailPanelCT);
		
		listCT = new JList();
		listCT.setListData(vct);
		listCT.setFont(new Font("lucida console", Font.PLAIN, 16));
		JScrollPane listjspCT = new JScrollPane(listCT);
		
		s4           = new JButton("Remove");
		resulttextCT = new JTextField("Select account number");
		s4.setFont(new Font("lucida console", Font.BOLD, 16));
		resulttextCT.setEditable(false);
		
		lblPanelCT.add(nameCT);
		lblPanelCT.add(balCT);
		fldPanelCT.add(nametextCT);
		fldPanelCT.add(baltextCT);
		textPanelCT.add(lblPanelCT, BorderLayout.WEST);
		textPanelCT.add(fldPanelCT, BorderLayout.CENTER);
		btnPanelCT.add(resulttextCT, BorderLayout.CENTER);
		btnPanelCT.add(s4, BorderLayout.EAST);
		ctPanelCT.add(textPanelCT, BorderLayout.NORTH);
		ctPanelCT.add(detailPanelCT, BorderLayout.CENTER);
		ctPanelCT.add(btnPanelCT, BorderLayout.SOUTH);
		listPanelCT.add(listjspCT);
		bp4.add(listPanelCT, BorderLayout.WEST);
		bp4.add(ctPanelCT, BorderLayout.CENTER);
		jtp.add(str[3],bp4);
		listCT.addListSelectionListener(this);
		s4.addActionListener(this);
		
		// <-------------------------------------------------------------------------> //
		
		
		/*
		 * <-- Statement -->
		 */
		BankingPanel bp5             = new BankingPanel(15, 80, 15, 80);
		BankingPanel headPanelStat   = new BankingPanel(10,  5, 10,  5);
		detailPanelStat              = new BankingPanel( 5, 5,  5, 5);
		bp5.setLayout(new BorderLayout(10, 10));
		headPanelStat.setLayout(new BorderLayout(10, 10));
		detailPanelStat.setLayout(new GridLayout(1, 1, 10, 10));
		headPanelStat.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		detailPanelStat.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 5));
		
		aitextStat = new JTextField();
		s5 = new JButton("View");
		s5.setFont(new Font("lucida console", Font.BOLD, 16));
		
		headPanelStat.add(aitextStat, BorderLayout.CENTER);
		headPanelStat.add(s5, BorderLayout.EAST);
		bp5.add(headPanelStat, BorderLayout.NORTH);
		bp5.add(detailPanelStat, BorderLayout.CENTER);
		jtp.add(str[4],bp5);
		s5.addActionListener(this);
		/* <-- Statement ends --> */
	
		add(jtp,BorderLayout.CENTER);
	}
	
	public void focusGained(FocusEvent fe) {}
	
	public void focusLost(FocusEvent fe)
	{
		try
		{
			pst2_2.setString(1, aitextT.getText());
			ResultSet rs = pst2_2.executeQuery();
			rs.next();
			
			String name = rs.getString("name");
			double balance = rs.getDouble("balance");
			
			nametextT.setText(name);
			balancetextT.setText(String.valueOf(balance));
		}
		catch (SQLException | NumberFormatException e)
		{
			clearTransaction();
			JOptionPane.showMessageDialog(this, "Error : " + e.getMessage());
		}
	}
	
	public void itemStateChanged(ItemEvent ie)
	{		
		Object source = ie.getItemSelectable();
		String str = ((JCheckBox) source).getText();
		str = (str.substring(0, 3)).trim();
		int index;
		
		if (ie.getStateChange() == ItemEvent.DESELECTED)
		{
			((JCheckBox) source).setSelected(false);
			index = tid.indexOf(str);
			if (index >= 0)
				tid.remove(index);
		}
		else if (ie.getStateChange() == ItemEvent.SELECTED)
		{
			((JCheckBox) source).setSelected(true);
			tid.add(str);
		}
	}
	
	public void populateDetailPanel() throws SQLException
	{
		ResultSet res;
		int size = 0;
		
		// setting the data for fields
		pst2_2.setInt(1, t_id);
		res = pst2_2.executeQuery();
		res.next();
		nametextCT.setText(res.getString("name"));
		baltextCT.setText(String.valueOf(res.getDouble("balance")));
		
		pst4.setInt(1, t_id);
		res = pst4.executeQuery();
		
		while (res.next())
			size++;
		
		if (size > 0)
		{
			tid = new Vector<String>();
			detailPanelCT.removeAll();
			bp = new BankingPanel[size];
			jcb = new JCheckBox[size];
			int i = 0;
			res.first();
			
			do
			{
				bp[i] = new BankingPanel(5, 5, 5, 5);
				bp[i].setLayout(new BorderLayout(5, 10));
				String s = String.format("%3d  %3d  %6s  %10.2f  %6s", res.getInt("trans_id"), res.getInt("accnt_id"),
										 res.getString("type"), res.getDouble("amount"), res.getString("status"));
				jcb[i] = new JCheckBox(s);
				bp[i].add(jcb[i], BorderLayout.CENTER);
				bp[i].setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
				jcb[i].setFont(new Font("lucida console", Font.PLAIN, 14));
				jcb[i].addItemListener(this);
				detailPanelCT.add(bp[i]);
				detailPanelCT.revalidate();
				i++;
			}
			while (res.next() && i <= size);
		}
		else
			detailPanelCT.removeAll();
		
		detailPanelCT.repaint();
	}
	
	public void populateDetailPanelStat() throws SQLException
	{
		JList statList = new JList();
		statList.setFont(new Font("lucida console", Font.PLAIN, 17));
		JScrollPane statJsp = new JScrollPane(statList);
		Vector<String> statVct = new Vector<String>();
		ResultSet res;
		int size = 0;
		int statId = Integer.parseInt(aitextStat.getText());
		
		pst4.setInt(1, statId);
		res = pst4.executeQuery();
		
		while (res.next())
			size++;
		
		if (size > 0)
		{
			detailPanelStat.removeAll();
			res.first();
			
			do
			{
				String s = String.format("%3d  %3d  %6s  %10.2f  %6s", res.getInt("trans_id"), res.getInt("accnt_id"),
										 res.getString("type"), res.getDouble("amount"), res.getString("status"));
				statVct.add(s);
			}
			while (res.next());
			
			statList.setListData(statVct);
			detailPanelStat.add(statJsp);
			detailPanelStat.revalidate();
			detailPanelStat.repaint();
		}
		else
		{
			detailPanelStat.removeAll();
			JOptionPane.showMessageDialog(this, "Records not found");
		}
		
		detailPanelStat.repaint();
	}
	
	public void valueChanged(ListSelectionEvent lse)
	{
		try
		{
			if (!lse.getValueIsAdjusting() && lse.getSource() == listCT && !lockCT)
			{
				t_id = Integer.parseInt((String) listCT.getSelectedValue());
				
				// method to populate detailPanelCT
				populateDetailPanel();
			}
			
			if (!lse.getValueIsAdjusting() && !lock && lse.getSource() == list)
			{
				s2.setEnabled(true);
				id = Integer.parseInt((String) list.getSelectedValue());
				pst2_2.setInt(1, id);
				ResultSet rs = pst2_2.executeQuery();
				rs.next();
				aitextRA.setText(String.valueOf(rs.getInt("accnt_id")));
				nametextRA.setText(rs.getString("name"));
				balancetextRA.setText(String.valueOf(rs.getDouble("balance")));
				dojtextRA.setText(rs.getString("doj"));
			}
		}
		catch (SQLException | NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Error : " + e.getMessage());
		}
	}
	
	public void clearNewAccount()
	{
		aitext.setText(String.valueOf(++lastId));
		nametext.setText("");
		balancetext.setText("");
	}
	
	public void clearRetireAccount()
	{
		aitextRA.setText("");
		dojtextRA.setText("");
		nametextRA.setText("");
		balancetextRA.setText("");
		s2.setEnabled(false);
	}
	
	public void clearTransaction()
	{
		aitextT.setText("");
		nametextT.setText("");
		balancetextT.setText("");
		amttextT.setText("");
		br.clearSelection();
	}
	
	public void clearCancelTransaction()
	{
		nametextCT.setText("");
		baltextCT.setText("");
		detailPanelCT.removeAll();
		detailPanelCT.repaint();
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		try
		{
			// <---------- New Account --------------------------------------------------> //
			
			if (ae.getSource() == s1)
			{
				lock   = true;
				lockCT = true;
				
				// entering the data into Customer table
				pst1.setInt(1, lastId);
				pst1.setString(2, nametext.getText());
				pst1.setDouble(3, Double.parseDouble(balancetext.getText()));
				pst1.setString(4, date);	
				pst1.executeUpdate();
				
				JOptionPane.showMessageDialog(this, "Data inserted successfully");
				
				// clearing the fields, resetting list and listCT
				clearNewAccount();
				
				// clearing the old list in retire account
				list.setListData(new Vector());
				vct = new Vector<String>();
				
				// re-populating the list in retire account
				ResultSet rs = pst2_1.executeQuery();
				while (rs.next())
				{
					String tid = String.format("%d", rs.getInt("accnt_id"));
					vct.add(tid);
				}
				list.setListData(vct);
				
				// re-populating the list in cancel transaction
				listCT.setListData(vct);
			
				lock   = false;
				lockCT = false;
			}
			else if (ae.getSource() == c1)
			{
				clearNewAccount();
			}
			
			
			// <---------- Retire Account -----------------------------------------------> //
			
			if (ae.getSource() == s2)
			{
				lock   = true;
				lockCT = true;
				
				ResultSet rs;
				int index = vct.indexOf(String.valueOf(id));
				
				pst2.setString(1, String.valueOf(id));
				pst2.executeUpdate();
				
				JOptionPane.showMessageDialog(this, "Details deleted successfully");
				
				// removing the items in the list.
				list.setListData(new Vector());
				
				// reinitialise the list
				vct.remove(index);
				list.setListData(vct);
				
				// clearing the fields
				clearRetireAccount();
				
				/*
				// Filling the fields with the details of next account than
				// the one that was currently deleted
				ResultSet rs = pst2_1.executeQuery();
				if (rs.next())
				{
					while (!rs.isLast() && rs.getInt("accnt_id") < id)
						rs.next();

					aitextRA.setText(String.valueOf(rs.getInt("accnt_id")));
					nametextRA.setText(rs.getString("name"));
					balancetextRA.setText(String.valueOf(rs.getDouble("balance")));
					dojtextRA.setText(rs.getString("doj"));
				}
				else 
					clearRetireAccount();
				*/
				
				// setting the account id for new account (in case the last id is removed).
				rs = pstAID.executeQuery();
				rs.next();
				lastId = rs.getInt("maxId");
				if (lastId == 0)
					lastId = 101;
				else
					lastId++;
				aitext.setText(String.valueOf(lastId));
				
				// re-populating the cancel transaction list.
				pst4_1.setString(1, String.valueOf(id));
				pst4_1.executeUpdate();
				listCT.setListData(vct);
				
				// clearing the transaction and cancel transaction fields
				clearTransaction();
				clearCancelTransaction();
				
				lock   = false;
				lockCT = false;
			}
			else if (ae.getSource() == c2)
			{
				clearRetireAccount();
			}
			
			
			// <---------- Transaction --------------------------------------------------> //
			
			if (ae.getSource() == s3) 
			{
				double amt = Double.parseDouble(amttextT.getText());
				double bal = Double.parseDouble(balancetextT.getText());
				String type = "", status = "";
				double sum = 0;
				
				if (jrbT1.isSelected())
				{
					type = "Credit";
					status = "SUCCESS";
					sum = amt + bal;
				}
				else if (jrbT2.isSelected())
				{
					type = "Debit";
					if (amt > bal)
						status = "FAILED";
					else 
					{	
						status = "SUCCESS";
						sum = bal - amt;
					}
				}
				
				pst3.setString(1, String.valueOf(lastTId));
				pst3.setString(2, aitextT.getText());
				pst3.setString(3, type);
				pst3.setString(4, String.valueOf(amt));
				pst3.setString(5, status);
				pst3.executeUpdate();
				
				if (status.equals("SUCCESS"))
				{
					pst3_2.setString(1, String.valueOf(sum));
					pst3_2.setString(2, aitextT.getText());
					pst3_2.executeUpdate();
					
					JOptionPane.showMessageDialog(this, "Transaction succesful");
					clearTransaction();
				}
				else {
					JOptionPane.showMessageDialog(this, "Transaction failed");
					clearTransaction();
				}
				transidtextT.setText(String.valueOf(++lastTId));
			}
			else if (ae.getSource() == c3)
			{
				clearTransaction();
			}
			
			
			// <---------- Cancel Transaction -------------------------------------------> //
			
			if (ae.getSource() == s4)
			{
				ResultSet rs1, rs2;
				int len = tid.size();
				int transId;
				double total = 0;
				
				for (int i = 0; i < len; i++)
				{
					transId = Integer.parseInt(String.valueOf(tid.get(i)));
					
					pst3_1.setString(1, String.valueOf(transId));
					rs1 = pst3_1.executeQuery();
					rs1.next();
					
					String type = rs1.getString("type");
					String status = rs1.getString("status");
				
					if (type.equals("Credit"))
					{
						total -= rs1.getDouble("amount");
					}
					else if (type.equals("Debit") && status.equals("SUCCESS"))
					{
						total += rs1.getDouble("amount");
					}
			
					pst2_2.setInt(1, t_id);
					rs2 = pst2_2.executeQuery();
					rs2.next();
					
					pst4_3.setDouble(1, rs2.getDouble("balance") + total);
					pst4_3.setInt(2, t_id);
					pst4_3.executeUpdate();
					
					pst4_2.setInt(1, transId);
					pst4_2.executeUpdate();
					
					JOptionPane.showMessageDialog(this, "Transaction deleted successfully");
					
					//populate detailPanelCT
					populateDetailPanel();
				}
			}
			
			if (ae.getSource() == s5)
			{
				populateDetailPanelStat();
			}
		}
		catch (SQLException | NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Error : " + e.getMessage());
		}
	}
}

class BankingPanel extends JPanel
{
	int top, right, bottom, left;
	
	BankingPanel(int top, int right, int bottom, int left)
	{
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	public Insets getInsets()
	{
		return new Insets(top, right, bottom, left);
	}
}

class Banking extends JFrame
{
	public static void main(String args[])
	{	
		Connection con = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking", "root", "dmatics");
		}
		catch (SQLException e)
		{
			try
			{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "dmatics");
				Statement st = con.createStatement();
				st.executeUpdate("CREATE DATABASE banking;");
				
				st.executeUpdate("create table Customer (accnt_id integer(5), name varchar(25), balance double, doj Date);");
				
				st.executeUpdate("create table Transaction (trans_id int(5), acct_id int(5), type varchar(10), amount double, status varchar(10));");
			}
			catch (SQLException se) {}
		}
		catch (ClassNotFoundException e) {}
		
		BankingFrame bf = new BankingFrame(con);
		bf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bf.setVisible(true);
	}
}