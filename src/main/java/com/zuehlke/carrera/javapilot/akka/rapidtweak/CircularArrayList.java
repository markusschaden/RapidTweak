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
        return super.get(index % size());
    }
}
