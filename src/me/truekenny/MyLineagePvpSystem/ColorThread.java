package me.truekenny.MyLineagePvpSystem;

class ColorThread extends Thread {
    private Players players;
    private boolean destroy = false;

    public ColorThread(Players players, String str) {
        super(str);
        this.players = players;
    }

    public void run() {
        while (true) {
            if (destroy) {
                break;
            }

            players.lookingForUpdateColor();

            try {
                sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void setDestroyTrue() {
        destroy = true;
    }
}