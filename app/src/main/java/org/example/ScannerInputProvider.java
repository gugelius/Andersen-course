package org.example;

import java.util.Scanner;

public class ScannerInputProvider implements InputProvider {
    private final Scanner scanner;

    public ScannerInputProvider(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public float nextFloat() {
        return scanner.nextFloat();
    }

    @Override
    public boolean nextBoolean() {
        return scanner.nextBoolean();
    }

    @Override
    public int nextInt() {
        return scanner.nextInt();
    }
}
