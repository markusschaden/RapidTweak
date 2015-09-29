package com.zuehlke.carrera.javapilot.akka.rapidtweak.track;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E>
{
    private static final long serialVersionUID = 1L;

    public E get(int index)
    {
        return super.get(index % size());
    }
}
