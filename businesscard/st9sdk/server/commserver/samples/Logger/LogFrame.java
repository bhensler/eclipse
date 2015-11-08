/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2002, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import com.lotus.sametime.placessa.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * The frame on which we print out the logging info.
 */
public class LogFrame extends JFrame
{
    /**
     * The table.
     */
    private JTable m_table;

    /**
     * The table model.
     */
    private MyTableModel m_tableModel;

    /**
     * Constructor.
     */
    public LogFrame()
    {
        super("Places Logger");

        m_tableModel = new MyTableModel();
        m_table = new JTable(m_tableModel);

        JScrollPane scrollPane = new JScrollPane(m_table);
        m_table.setPreferredScrollableViewportSize(new Dimension(650, 70));

        m_table.setRowSelectionAllowed(false);
        m_table.setCellSelectionEnabled(false);
        m_table.setRowHeight(30);


        setColumns();

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent event)
                {
                    setVisible(false);
                    System.exit(0);
                }

            });
    }

    /**
     * A new place has been created. Add a line to the table.
     *
     * @param handler The place handler object.
     */
    public void add(PlaceHandler handler)
    {
        m_tableModel.addRow(handler);
    }

    /**
     * Time is updated, refresh the correct cell.
     */
    public void refreshTime(PlaceHandler handler)
    {
        m_tableModel.refreshTime(handler);
    }

    /**
     * Table data has changed, refresh it.
     */
    public void refreshAll()
    {
        m_tableModel.refresh();
    }

    /**
     * Set the columns width.
     */
    void setColumns()
    {
        TableColumn column = null;
        for (int i = 0; i < m_tableModel.getColumnCount(); i++)
        {
            column = m_table.getColumnModel().getColumn(i);
            if (i == 0)
            {
                // The first column should be wider.
                column.setPreferredWidth(100);
            }
            else
            {
                column.setPreferredWidth(35);
            }
        }
    }
}

/**
 * A custom model for our table.
 */
class MyTableModel extends AbstractTableModel
{
    //
    // Members.
    //

    /**
     * The number of rows.
     */
    protected int numRows = 0;

    /**
     * The initial number of rows. (So the table won't be gray).
     */
    final static int INITIAL_ROWS = 3;

     /**
      * Column names.
      */
    static protected String[] cNames = {"Place Name", "Time",
                                        "Chat", "Audio", "Video", "Share",
                                        "Whiteboard"};
    /**
     * The table data.
     */
    protected Vector m_data = new Vector();

    //
    // Table model implementations.
    //

    /**
     * @return The column name.
     */
    public String getColumnName(int column)
    {
        return cNames[column];
    }

    /**
     * @return the number of columns.
     */
    public int getColumnCount()
    {
        return cNames.length;
    }

    /**
     * @return The number of rows.
     */
    public int getRowCount()
    {
        return numRows;
    }

    /**
     * @return The contents of the given cell.
     */
    public Object getValueAt(int row, int column)
    {
        if (m_data.size() <= row)
        {
            return "";
        }

        PlaceHandler p = (PlaceHandler)m_data.elementAt(row);
        switch (column)
        {
            case 0:
                return p.getName();
            case 1:
                return p.getTime();
            case 2:
                return p.hasChat();
            case 3:
                return p.hasAudio();
            case 4:
                return p.hasVideo();
            case 5:
                return p.hasShare();
            case 6:
                return p.hasWhiteboard();
            default:
                return "";
        }
    }

    /**
     * @return The class type of a column.
     */
    public Class getColumnClass(int c)
    {
        return getValueAt(0,c).getClass();
    }

    //
    //
    //

    /**
     * Refresh the time cell for the given place.
     * We don't want to refresh the whole table every timer click.
     */
    public void refreshTime(PlaceHandler handler)
    {
        int row = m_data.indexOf(handler);
        fireTableCellUpdated(row, 1);
    }

    /**
     * Refresh the whole table.
     */
    public void refresh()
    {
        fireTableDataChanged();
    }

    /**
     * Add another row to the table.
     */
    public synchronized void addRow(PlaceHandler place)
    {
        numRows++;
        m_data.addElement(place);

        //Notify listeners that the data changed.
	    fireTableRowsInserted(numRows-1, numRows-1);
    }
}

