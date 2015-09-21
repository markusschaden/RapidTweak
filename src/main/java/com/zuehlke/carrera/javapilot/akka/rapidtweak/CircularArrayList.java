package com.zuehlke.carrera.javapilot.akka.rapidtweak;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 21.09.2015.
 */
public class CircularArrayList<E> extends ArrayList<E>
{
    private static final long serialVersionUID = 1L;

    public E get(int index)
    {
        if (index == -1)
        {
            index = size()-1;
        }

        else if (index == size())
        {
            index = 0;
        }

        return super.get(index);
    }
}
