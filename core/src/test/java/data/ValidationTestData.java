package data;

public class ValidationTestData {

  public static class Person {

    public final String name;
    public final int age;
    public final Cat cat;

    public Person(String name, int age, Cat cat) {
      this.name = name;
      this.age = age;
      this.cat = cat;
    }
  }

  public static class Cat {

    public final String sound;
    public final int hungryPercentage;

    public Cat(String sound, int hungryPercentage) {
      this.sound = sound;
      this.hungryPercentage = hungryPercentage;
    }
  }
}
