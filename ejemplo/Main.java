package ejemplo;
interface Phone {
    String getModel();
}

interface Computer {
    String getModel();
}

class SamsungPhone implements Phone {
    public String getModel() {
        return "Samsung Galaxy";
    }
}

class ApplePhone implements Phone {
    public String getModel() {
        return "iPhone";
    }
}

class SamsungComputer implements Computer {
    public String getModel() {
        return "Samsung Notebook";
    }
}

class AppleComputer implements Computer {
    public String getModel() {
        return "MacBook";
    }
}

interface DeviceFactory {
    Phone createPhone();
    Computer createComputer();
}

class SamsungFactory implements DeviceFactory {
    public Phone createPhone() {
        return new SamsungPhone();
    }

    public Computer createComputer() {
        return new SamsungComputer();
    }
}

class AppleFactory implements DeviceFactory {
    public Phone createPhone() {
        return new ApplePhone();
    }

    public Computer createComputer() {
        return new AppleComputer();
    }
}

public class Main {
    public static void main(String[] args) {
        DeviceFactory samsungFactory = new SamsungFactory();
        Phone samsungPhone = samsungFactory.createPhone();
        Computer samsungComputer = samsungFactory.createComputer();

        System.out.println("Samsung Phone Model: " + samsungPhone.getModel());
        System.out.println("Samsung Computer Model: " + samsungComputer.getModel());

        DeviceFactory appleFactory = new AppleFactory();
        Phone applePhone = appleFactory.createPhone();
        Computer appleComputer = appleFactory.createComputer();

        System.out.println("Apple Phone Model: " + applePhone.getModel());
        System.out.println("Apple Computer Model: " + appleComputer.getModel());
    }
}