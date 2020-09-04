package com.company;

import javax.swing.*;
import java.awt.*;

class ImageListCellRenderer implements ListCellRenderer
{
    private Color veryLightGrey = new Color(238,238,238);
    /**
     * From http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html:
     *
     * Return a component that has been configured to display the specified value.
     * That component's paint method is then called to "render" the cell.
     * If it is necessary to compute the dimensions of a list because the list cells do not have a fixed size,
     * this method is called to generate a component on which getPreferredSize can be invoked.
     *
     * jlist - the jlist we're painting
     * value - the value returned by list.getModel().getElementAt(index).
     * cellIndex - the cell index
     * isSelected - true if the specified cell is currently selected
     * cellHasFocus - true if the cell has focus
     */
    public Component getListCellRendererComponent(JList jlist,
                                                  Object value,
                                                  int cellIndex,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        Component component = (Component) value;
        component.setForeground (Color.lightGray);
        component.setBackground (isSelected ? Color.lightGray : veryLightGrey);
        return component;
    }
}
