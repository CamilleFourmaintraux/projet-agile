package main.java;

public class UnknownMenu extends GameMenu {
  private final String message;

  public UnknownMenu(String message) {
    this.message = message;
  }

  public UnknownMenu() {
    this.message = "Wooooow t'as foutu quoi! T'as encore pété le jeuuu jpp!!";
  }

  @Override
  protected void display() {
    Controls.println(message);   
    displayQuitMessage(); 
  }
}
