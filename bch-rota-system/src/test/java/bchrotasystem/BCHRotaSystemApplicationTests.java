package bchrotasystem;

import bchrotasystem.Service.ShiftService;
import bchrotasystem.Service.ShiftTypeService;
import bchrotasystem.Service.UserService;
import bchrotasystem.entity.Shift;
import bchrotasystem.entity.ShiftType;
import bchrotasystem.entity.User;
//import jdk.vm.ci.meta.Local;
import com.jayway.jsonpath.internal.function.numeric.Sum;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class BCHRotaSystemApplicationTests {

	@Autowired
	ShiftService shiftService;

	@Autowired
	UserService userService;

	@Autowired
	ShiftTypeService shiftTypeService;


	@Test
	@Order(1)
	void contextLoads() {
	}

	@Test
	@Order(2)
	void ClearDB() {
		shiftService.deleteAllShifts();
		userService.deleteAllUsers();
		shiftTypeService.deleteAllShiftTypes();
		long count = (userService.countUsers() + shiftTypeService.countShiftTypes() + shiftService.countShifts());
		assert (count == 0);
	}

	@Test
	@Order(3)
	void addOneShift() {
		shiftService.deleteAllShifts();
		shiftService.addShift(new Shift());
		assert (shiftService.countShifts() == 1);
	}

	@Test
	@Order(4)
	void addManyShifts() {
		shiftService.deleteAllShifts();
		shiftService.addShift(new Shift());
		shiftService.addShift(new Shift());
		shiftService.addShift(new Shift());
		assert (shiftService.countShifts() == 3);
	}
	@Test
	@Order(5)
	void addManyShiftsTogether() {
		shiftService.deleteAllShifts();
		List<Shift> shifts2 = new ArrayList<>();
		shifts2.add(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shifts2.add(new Shift(LocalDate.of(2020,1,5),2,2));
		shifts2.add(new Shift(LocalDate.of(2020,1,5),3,3));
		shiftService.addShifts(shifts2);
		assert (shiftService.countShifts() == 3);
	}

	@Test
	@Order(6)
	void addOneShiftType() {
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType());
		assert (shiftTypeService.countShiftTypes() == 1);
	}

	@Test
	@Order(7)
	void addManyShiftTypes() {
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType());
		shiftTypeService.addShiftType(new ShiftType());
		shiftTypeService.addShiftType(new ShiftType());
		assert (shiftTypeService.countShiftTypes() == 3);
	}

	@Test
	@Order(8)
	void addOneUser() {
		userService.deleteAllUsers();
		userService.addUser(new User());
		assert (userService.countUsers() == 1);
	}

	@Test
	@Order(9)
	void addManyUsers() {
		userService.deleteAllUsers();
		userService.addUser(new User());
		userService.addUser(new User());
		userService.addUser(new User());
		assert (userService.countUsers() == 3);
	}

	@Test
	@Order(10)
	void getShiftsByDate() {
		shiftService.deleteAllShifts();
		shiftService.addShift(new Shift());
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),3,3,"Ill"));
		List<Shift> shifts;
		List<Shift> TestShifts = new ArrayList<>();
		TestShifts.add(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		TestShifts.add(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
		TestShifts.add(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
		shifts = shiftService.getShiftsByDate(LocalDate.of(2020,1,5));
		int countFound = 0;
		for (Shift currentShift : shifts) {
			for (Shift testShift : TestShifts) {
				if (shiftService.shiftsEqual(currentShift, testShift)) {
					countFound++;
				}
			}
		}
		assert (countFound == 3 && shifts.size() == 3);
	}

	@Test
	@Order(11)
	void getNoShiftsByDate() {
		shiftService.deleteAllShifts();
		shiftService.addShift(new Shift());
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),3,3,"Ill"));
		List<Shift> shifts;
		shifts = shiftService.getShiftsByDate(LocalDate.of(2020,1,6));
		assert (shifts.size() == 0);
	}


	@Test
	@Order(12)
	void deleteShift() {
		shiftService.deleteAllShifts();
		//shiftService.addShift(new Shift());
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
		shiftService.deleteShift(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		//List<Shift> shifts = new ArrayList<>();
		//shifts = shiftService.getShiftList();
		assert (shiftService.countShifts() == 2);
	}

	@Test
	@Order(13)
	void deleteShiftType() {
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType());
		shiftTypeService.addShiftType(new ShiftType("Morning", 100,200));
		shiftTypeService.addShiftType(new ShiftType("Evening", 200,300));
		shiftTypeService.deleteShiftType(new ShiftType("Evening", 200,300));
		//List<ShiftType> shiftTypes = new ArrayList<>();
		//shiftTypes = shiftTypeService.getShiftTypeList();
		assert (shiftTypeService.countShiftTypes() == 2);
	}

	@Test
	@Order(14)
	void deleteUser() {
		userService.deleteAllUsers();
		userService.addUser(new User());
		userService.addUser(new User("Alex","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("Barry","Welsh", 0));
		userService.deleteUser(new User("Alex","Welsh", 1,1, "aaa", "AAA"));
		//List<User> Users = new ArrayList<>();
		//Users = userService.getUserList();
		assert (userService.countUsers() == 2);
	}

	@Test
	@Order(15)
	void deleteShifts() {
		shiftService.deleteAllShifts();
		//shiftService.addShift(new Shift());
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),1,1,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),2,2,"Ill"));
		shiftService.addShift(new Shift(LocalDate.of(2020,4,14),3,3,"Ill"));
		List<Shift> shifts = new ArrayList<>();
		shifts.add(new Shift(LocalDate.of(2020,1,5),1,1,"Ill"));
		shifts.add(new Shift(LocalDate.of(2020,4,14),2,2,"Ill"));
		shiftService.deleteShifts(shifts);
		assert (shiftService.countShifts() == 4);
	}

	@Test
	@Order(16)
	void deleteShiftTypes() {
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType());
		shiftTypeService.addShiftType(new ShiftType("Morning", 100,200));
		shiftTypeService.addShiftType(new ShiftType("Afternoon", 200,300));
		shiftTypeService.addShiftType(new ShiftType("Evening", 300,400));
		shiftTypeService.addShiftType(new ShiftType("Night", 400,500));
		List<ShiftType> shiftTypes = new ArrayList<>();
		shiftTypes.add(new ShiftType("Afternoon", 200,300));
		shiftTypes.add(new ShiftType("Evening", 300,400));
		shiftTypeService.deleteShiftTypes(shiftTypes);
		assert (shiftTypeService.countShiftTypes() == 3);
	}

	@Test
	@Order(17)
	void deleteUsers() {
		userService.deleteAllUsers();
		userService.addUser(new User());
		userService.addUser(new User("Alex","Welsh", 0));
		userService.addUser(new User("Barry","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("David","Welsh", 0));
		List<User> Users = new ArrayList<>();
		Users.add(new User("Barry","Welsh", 1,1, "aaa", "AAA"));
		Users.add(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		userService.deleteUsers(Users);
		assert (userService.countUsers() == 3);
	}

	@Test
	@Order(18)
	void getIDByName(){
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType("TestShift1", 100,200));
		shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
		shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
		List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
		Integer searchID = 0;
		for (ShiftType temp : shiftTypes) {
			if (temp.getName().equals("TestShift1")) searchID = temp.getId();
		}
		assert (shiftTypeService.getIDByName("TestShift1").equals(searchID));
	}

	@Test
	@Order(19)
	void getBasicUserList(){
		userService.deleteAllUsers();
		userService.addUser(new User("Alex","Welsh", 1));
		userService.addUser(new User("Barry","Welsh", 0));
		userService.addUser(new User("Charlie","Welsh", 1));
		userService.addUser(new User("David","Welsh", 2,1, "aaa", "AAA"));
		userService.addUser(new User("Eric","Welsh", 2,1, "aaa", "AAA"));
		userService.addUser(new User("Frank","Welsh", 3));
		List<User> users;
		List<User> testUsers = new ArrayList<>();
		testUsers.add(new User("David","Welsh", 2,1, "aaa", "AAA"));
		testUsers.add(new User("Eric","Welsh", 2,1, "aaa", "AAA"));
		users = userService.getBasicUserList();
		int countFound = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound++;
				}
			}
		}
		assert (countFound == 2);
	}

	@Test
	@Order(20)
	void getShiftTypeNameFromShift(){
		shiftService.deleteAllShifts();
		shiftTypeService.deleteAllShiftTypes();
		shiftTypeService.addShiftType(new ShiftType("TestShift1", 100,200));
		shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
		shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
		Integer shiftTypeID = shiftTypeService.getIDByName("TestShift2");
		shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,shiftTypeID,"Ill"));
		List<Shift> theShift;
		theShift = shiftService.getShiftList();
		assert ("TestShift2".equals(shiftService.getShiftTypeName(theShift.get(0))));
	}

	@Test
	@Order(21)
    void getUserFromShift(){
	    shiftService.deleteAllShifts();
	    userService.deleteAllUsers();
        userService.addUser(new User("Alex","Welsh", 1));
        userService.addUser(new User("Barry","Welsh", 1,1, "aaa", "AAA"));
        userService.addUser(new User("Charlie","Welsh", 1));
        Integer userID = userService.getIDByNames("Barry","Welsh");
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),userID,1,"Ill"));
        List<Shift> theShift;
        theShift = shiftService.getShiftList();
        assert (userService.usersEqual(shiftService.getUser(theShift.get(0)), new User("Barry","Welsh", 1,1, "aaa", "AAA")));
    }

    @Test
	@Order(22)
    void getShiftTypeFromShift(){
        shiftService.deleteAllShifts();
        shiftTypeService.deleteAllShiftTypes();
        shiftTypeService.addShiftType(new ShiftType("TestShift1", 100,200));
        shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
        shiftTypeService.addShiftType(new ShiftType("TestShift2", 100,200));
        Integer shiftTypeID = shiftTypeService.getIDByName("TestShift2");
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),1,shiftTypeID,"Ill"));
        List<Shift> theShift;
        theShift = shiftService.getShiftList();
        assert (shiftTypeService.shiftTypesEqual(shiftService.getShiftType(theShift.get(0)),new ShiftType("TestShift2", 100,200)));
    }

    @Test
	@Order(23)
    void getUserForenameViaShift(){
        shiftService.deleteAllShifts();
        userService.deleteAllUsers();
        userService.addUser(new User("Alex","Welsh", 1));
        userService.addUser(new User("Barry","Welsh", 0));
        userService.addUser(new User("Charlie","Welsh", 1));
        Integer userID = userService.getIDByNames("Barry","Welsh");
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),userID,1,"Ill"));
        List<Shift> theShift;
        theShift = shiftService.getShiftList();
        assert (shiftService.getUserFirstName(theShift.get(0)).equals("Barry"));
    }
    @Test
	@Order(24)
    void getUserSurnameViaShift(){
        shiftService.deleteAllShifts();
        userService.deleteAllUsers();
        userService.addUser(new User("Alex","Irish", 1));
        userService.addUser(new User("Barry","Welsh", 0));
        userService.addUser(new User("Charlie","Scottish", 1));
        Integer userID = userService.getIDByNames("Barry","Welsh");
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),userID,1,"Ill"));
        List<Shift> theShift;
        theShift = shiftService.getShiftList();
        assert (shiftService.getUserLastName(theShift.get(0)).equals("Welsh"));
    }

    @Test
	@Order(25)
    void getShiftsByName() {
        shiftService.deleteAllShifts();
        userService.deleteAllUsers();
        userService.addUser(new User("Alex","Irish", 1));
        userService.addUser(new User("Barry","Welsh", 0));
        userService.addUser(new User("Charlie","Scottish", 1));
        Integer tempID = userService.getIDByNames("Alex","Irish");
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),tempID,1,"Ill"));
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),2,2,"Ill"));
        shiftService.addShift(new Shift(LocalDate.of(2020,1,5),3,3,"Ill"));
        shiftService.addShift(new Shift(LocalDate.of(2020,4,14),1,1,"Ill"));
        shiftService.addShift(new Shift(LocalDate.of(2020,4,14),tempID,2,"Ill"));
        shiftService.addShift(new Shift(LocalDate.of(2020,4,14),3,3,"Ill"));
        List<Shift> shifts;
        List<Shift> TestShifts = new ArrayList<>();
        TestShifts.add(new Shift(LocalDate.of(2020,4,14),tempID,2,"Ill"));
        TestShifts.add(new Shift(LocalDate.of(2020,1,5),tempID,1,"Ill"));
        shifts = shiftService.getShiftsByName("Alex", "Irish");
        int countFound = 0;
        for (Shift currentShift : shifts) {
            for (Shift testShift : TestShifts) {
                if (shiftService.shiftsEqual(currentShift, testShift)) {
                    countFound++;
                }
            }
        }
        assert (countFound == 2);
    }

	@Test
	@Order(26)
	void getUsersByAccLevel(){
		userService.deleteAllUsers();
		userService.addUser(new User("Alex","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("Barry","Welsh", 0,1, "aaa", "AAA"));
		userService.addUser(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("David","Welsh", 2,1, "aaa", "AAA"));
		userService.addUser(new User("Eric","Welsh", 2,1, "aaa", "AAA"));
		userService.addUser(new User("Frank","Welsh", 3,1, "aaa", "AAA"));
		List<User> users;
		List<User> testUsers0 = new ArrayList<>();
		List<User> testUsers1 = new ArrayList<>();
		List<User> testUsers2 = new ArrayList<>();
		List<User> testUsers3 = new ArrayList<>();
		testUsers0.add(new User("Barry","Welsh", 0,1, "aaa", "AAA"));
		testUsers1.add(new User("Alex","Welsh", 1,1, "aaa", "AAA"));
		testUsers1.add(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		testUsers2.add(new User("David","Welsh", 2,1, "aaa", "AAA"));
		testUsers2.add(new User("Eric","Welsh", 2,1, "aaa", "AAA"));
		testUsers3.add(new User("Frank","Welsh", 3,1, "aaa", "AAA"));

		users = userService.getUsersByAccLevel(0);
		int countFound0 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers0) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound0++;
				}
			}
		}
		users = userService.getUsersByAccLevel(1);
		int countFound1 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers1) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound1++;
				}
			}
		}
		users = userService.getUsersByAccLevel(2);
		int countFound2 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers2) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound2++;
				}
			}
		}
		users = userService.getUsersByAccLevel(3);
		int countFound3 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers3) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound3++;
				}
			}
		}
		assert (countFound0 + countFound1 + countFound2 + countFound3 == 6);
	}

	@Test
	@Order(27)
	void getUsersByRole(){
		userService.deleteAllUsers();
		userService.addUser(new User("Alex","Welsh", 1, 1, "aaa", "AAA"));
		userService.addUser(new User("Barry","Welsh", 0,0, "aaa", "AAA"));
		userService.addUser(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		userService.addUser(new User("David","Welsh", 2,2, "aaa", "AAA"));
		userService.addUser(new User("Eric","Welsh", 2,2, "aaa", "AAA"));
		userService.addUser(new User("Frank","Welsh", 3,3, "aaa", "AAA"));
		List<User> users;
		List<User> testUsers0 = new ArrayList<>();
		List<User> testUsers1 = new ArrayList<>();
		List<User> testUsers2 = new ArrayList<>();
		List<User> testUsers3 = new ArrayList<>();
		testUsers0.add(new User("Barry","Welsh", 0,0, "aaa", "AAA"));
		testUsers1.add(new User("Alex","Welsh", 1,1, "aaa", "AAA"));
		testUsers1.add(new User("Charlie","Welsh", 1,1, "aaa", "AAA"));
		testUsers2.add(new User("David","Welsh", 2,2, "aaa", "AAA"));
		testUsers2.add(new User("Eric","Welsh", 2,2, "aaa", "AAA"));
		testUsers3.add(new User("Frank","Welsh", 3,3, "aaa", "AAA"));

		users = userService.getUsersByRole(0);
		int countFound0 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers0) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound0++;
				}
			}
		}
		users = userService.getUsersByRole(1);
		int countFound1 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers1) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound1++;
				}
			}
		}
		users = userService.getUsersByRole(2);
		int countFound2 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers2) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound2++;
				}
			}
		}
		users = userService.getUsersByRole(3);
		int countFound3 = 0;
		for (User currentUser : users) {
			for (User testUser : testUsers3) {
				if (userService.usersEqual(currentUser, testUser)) {
					countFound3++;
				}
			}
		}
		assert (countFound0 + countFound1 + countFound2 + countFound3 == 6);
	}

	@Test
	@Order(28)
	void clearDBAgain(){
		userService.deleteAllUsers();
		shiftTypeService.deleteAllShiftTypes();
		shiftService.deleteAllShifts();
		assert((userService.countUsers() + shiftTypeService.countShiftTypes() + shiftService.countShifts()) == 0);
	}
}
