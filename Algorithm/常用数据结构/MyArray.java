package com.zozo.zozochina.ui.brand.view;

public class MyArray {
    private int[] array;
    private int size;

    public MyArray(int capacity) {
        this.array = new int[capacity];
        size = 0;
    }

    public void insert(int element, int index) throws Exception {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("超出数组实际元素范围！");
        }

        if (size >= array.length) {
            resize();
        }

        for (int i = size - 1; i >= index; i--) {
            array[i + 1] = array[i];
        }

        array[index] = element;
        size ++;
    }

    public int delete(int index) throws Exception {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("超出数组实际元素范围");
        }

        int deletedElement = array[index];

        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }

        size --;

        return deletedElement;
    }

    public void resize() {
        int[] arrayNew = new int[array.length * 2];
        System.arraycopy(array, 0, arrayNew, 0, array.length);
        array = arrayNew;
    }

    public void output() {
        for (int i = 0; i < size; i++) {
            System.out.println(array[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        MyArray myArray = new MyArray(4);
        myArray.insert(3, 0);
        myArray.insert(7, 1);
        myArray.insert(9, 2);
        myArray.insert(5, 3);
        myArray.insert(6, 1);
        myArray.output();
    }
}
