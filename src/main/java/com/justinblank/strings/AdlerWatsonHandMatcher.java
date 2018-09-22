package com.justinblank.strings;

public class AdlerWatsonHandMatcher {

    public AdlerWatsonHandMatcher(String s) {
        this.s = s;
    }

    private String s;
    private int counter;

    public boolean containedIn() {
        int state = 0;
        int length = s.length();
        int counter = 0;
        while (state == 0) {
            if (counter == length) {
                return wasAccepted(state);
            }
            char c = s.charAt(counter);
            counter++;
            if (c == 'A') {
                this.counter = counter;
                state = state1();
                counter = this.counter;
            }
            else if (c == 'W') {
                this.counter = counter;
                state = state2();
                counter = this.counter;
            }
        }
        return wasAccepted(state);
    }

    public boolean wasAccepted(int state) {
        return state == 3 || state == 4;
    }

    public int state1() {
        int counter = this.counter;
        int length = s.length();
        if (counter < length) {
            char c = s.charAt(counter);
            if (c == 'd') {
                counter++;
                if (counter < length) {
                    c = s.charAt(counter);
                    if (c == 'l') {
                        counter++;
                        if (counter < length) {
                            c = s.charAt(counter);
                            if (c == 'e') {
                                counter++;
                                if (counter < length) {
                                    c = s.charAt(counter);
                                    if (c == 'r') {
                                        this.counter = counter;
                                        return 3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.counter = counter;
        return -1;
    }

    public int state2() {
        int length = s.length();
        int counter = this.counter;
        if (counter < length) {
            char c = s.charAt(counter);
            counter++;
            if (c == 'a') {
                if (counter < length) {
                    c = s.charAt(counter);
                    counter++;
                    if (c == 't') {
                        if (counter < length) {
                            c = s.charAt(counter);
                            counter++;
                            if (c == 's') {
                                if (counter < length) {
                                    c = s.charAt(counter);
                                    counter++;
                                    if (c == 'o') {
                                        if (counter < length) {
                                            c = s.charAt(counter);
                                            counter++;
                                            if (c == 'n') {
                                                this.counter = counter;
                                                return 4;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.counter = counter;
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(new AdlerWatsonHandMatcher("Adler").containedIn());
        System.out.println(new AdlerWatsonHandMatcher("No Adler").containedIn());
        System.out.println(new AdlerWatsonHandMatcher("Watson").containedIn());
        System.out.println(new AdlerWatsonHandMatcher("No Watson").containedIn());
        System.out.println(new AdlerWatsonHandMatcher("GarbWage").containedIn());
        System.out.println(new AdlerWatsonHandMatcher("dler").containedIn());
    }
}
