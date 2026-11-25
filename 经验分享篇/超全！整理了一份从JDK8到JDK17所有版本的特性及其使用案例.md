# 超全！整理了一份从JDK8到JDK17所有版本的特性及其使用案例


## JDK 8 特性：
### 1. Lambda 表达式：
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(name -> System.out.println(name));
```

### 2. Stream API：
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n)
                .sum();
System.out.println(sum);
```

### 3. 接口默认方法：
```java
public interface MyInterface {
    default void doSomething() {
        System.out.println("Doing something");
    }
}
```

### 4. 方法引用：
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(System.out::println);
```

## JDK 9 特性：
### 1. 模块化系统：
```java
module com.example.myapp {
    requires java.base;
    requires java.sql;
    requires java.desktop;
}
```

### 2. 私有接口方法：
```java
public interface MyInterface {
    default void doSomething() {
        doInternal();
    }
    
    private void doInternal() {
        // 执行一些内部操作
    }
}
```

### 3. 改进的 try-with-resources：
```java
try (Resource1 res1 = new Resource1();
     Resource2 res2 = new Resource2()) {
    // 执行一些操作
} catch (Exception e) {
    // 处理异常
}
```

### 4. 集合工厂方法：
```java
List<String> names = List.of("Alice", "Bob", "Charlie");
Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);
```

## JDK 10 特性：
### 1. 局部变量类型推断：
```java
var name = "Alice";
var age = 30;
```

### 2. 改进的 Optional 类：
```java
Optional<String> optional = Optional.of("Hello");
optional.ifPresentOrElse(
    value -> System.out.println(value),
    () -> System.out.println("Value not present")
);
```

### 3. 并行全垃圾收集器：
```java
java -XX:+UseParallelGC Main
```

## JDK 11 特性：
### 1. HTTP 客户端：
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.example.com"))
        .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

### 2. 字符串新增方法：
```java
String str = " Hello ";
System.out.println(str.strip());
System.out.println(str.repeat(3));
```

### 3. 局部变量语法扩展：
```java
Map<Integer, String> map = Map.of(
    1, "One",
    2, "Two",
    3, "Three"
);
```

## JDK 12 特性：
### 1. Switch 表达式：
```java
int day = 3;
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday
    
```java
    case 4 -> "Thursday";
    case 5 -> "Friday";
    default -> "Invalid day";
};
System.out.println(dayName);
```

### 2. 新增的 String 方法：
```java
String str = "Hello, World!";
System.out.println(str.indent(4));
System.out.println(str.transform(String::toUpperCase));
```

### 3. 新的文件方法：
```java
Path path = Paths.get("path/to/file.txt");
boolean isRegularExecutableFile = Files.isRegularFile(path) && Files.isExecutable(path);
System.out.println(isRegularExecutableFile);
```

## JDK 13 特性：
### 1. 文本块：
```java
String html = """
              <html>
                  <body>
                      <h1>Hello, World!</h1>
                  </body>
              </html>
              """;
System.out.println(html);
```

### 2. Switch 表达式增强：
```java
String day = "Monday";
String dayType = switch (day) {
    case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
    case "Saturday", "Sunday" -> "Weekend";
    default -> throw new IllegalArgumentException("Invalid day");
};
System.out.println(dayType);
```

### 3. 新增的 String 方法：
```java
String str = "Hello, World!";
System.out.println(str.isBlank());
System.out.println(str.lines().count());
```

## JDK 14 特性：
### 1. Switch 表达式增强：
```java
String day = "Monday";
String dayType = switch (day) {
    case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
    case "Saturday", "Sunday" -> "Weekend";
    default -> {
        if (day.isEmpty()) {
            yield "No day";
        } else {
            yield "Invalid day";
        }
    }
};
System.out.println(dayType);
```

### 2. 新增的记录类型：
```java
record Person(String name, int age) {
    // 构造函数和访问器方法自动生成
}

Person person = new Person("Alice", 30);
System.out.println(person.name());
System.out.println(person.age());
```

### 3. 新增的 NullPointerException 提示：
```java
String name = null;
System.out.println(name.toLowerCase());  // 抛出 NullPointerException，并提示具体信息
```

## JDK 15 特性：
### 1. 新增的 sealed 类：
```java
sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
}

final class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double area() {
        return Math.PI * radius * radius;
    }
}

// 另外两个实现类的定义与 Circle 类似

Shape shape = new Circle(5.0);
System.out.println(shape.area());
```

### 2. Text Blocks 增强：
```java
String html = """
              <html>
                  <body>
                      <h1>Hello, World!</h1>
                  </body>
              </html>
              """.formatted();
System.out.println(html);
```

### 3. 新增的记录类型特性：
```java
record Person(String name, int age) {
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
}

Person person = new Person("Alice", -30);  // 抛出 IllegalArgumentException
```

## JDK 16 特性：
### 1. Pattern Matching for instanceof：
```java
Object obj instanceof String str -> {
    System.out.println(str.length());
}
```
### 2. 新增的 record 类特性：
```java
record Person(String name, int age) {
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Bob", 25);

        System.out.println(person1.equals(person2));  // 输出 false
        System.out.println(person1.name());  // 输出 "Alice"
        System.out.println(person1.age());  // 输出 30
        System.out.println(person1.toString());  // 输出 "Person[name=Alice, age=30]"
    }
}
```

### 3. 新增的 Unix 域套接字（Unix Domain Socket）支持：
```java
Path socketPath = Paths.get("/path/to/socket");
try (UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketPath);
     UnixDomainSocketChannel channel = UnixDomainSocketChannel.open()) {
    channel.connect(address);
    // 进行读写操作
} catch (IOException e) {
    // 处理异常
}
```

## JDK 17 特性：
### 1. 新增的 switch 表达式特性：
```java
String day = "Monday";
String dayType = switch (day) {
    case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> {
        yield "Weekday";
    }
    case "Saturday", "Sunday" -> {
        yield "Weekend";
    }
    default -> {
        if (day.isEmpty()) {
            yield "No day";
        } else {
            yield "Invalid day";
        }
    }
};
System.out.println(dayType);
```

### 2. 静态成员变量默认初始化：
```java
public class MyClass {
    static int count;

    public static void main(String[] args) {
        System.out.println(count);  // 输出 0
    }
}
```

### 3. 新的 JVM 选项：
```java
java -XX:+UseShenandoahGC Main
```