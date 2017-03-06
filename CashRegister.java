/**Class models a cash register, with attributes corresponding to the amount of
 * bills and loonies inside the register. Multiple methods including getter
 * methods, purchase item, refund item, and updateMoney methods. Four different
 * constructors are in the class, taking different types of input to create cash
 * registers in various ways.
 * 
 * @author HowieZhao
 *
 */
public class CashRegister {
	/* these static attributes keep a count of how many */
	/* times the updateMoney and allLoonies methods */
	/* are called (do not use these!) */
	public static int updateMoneyCount = 0;
	public static int allLooniesCount = 0;
	// added attributes which represent amount of each bill in the cash register
	private int loonies = 0;
	private int fives = 0;
	private int tens = 0;
	private int twenties = 0;
	private int fifties = 0;

	/* constructors */
	/* different ways of specifying the money amount */
	public CashRegister() {
		/* creates a register with zero money */
	}

	public CashRegister(int[] money) {
		/*
		 * creates a register with money specified in money array [loonies, 5s,
		 * 10s, 20s, 50s]
		 */
		this(money[0], money[1], money[2], money[3], money[4]);
	}

	public CashRegister(Money money) {
		this(money.getAll());
		/* creates a register with the money specified in the money object */
	}

	public CashRegister(int n1, int n5, int n10, int n20, int n50) {
		this.loonies = n1;
		this.fives = n5;
		this.tens = n10;
		this.twenties = n20;
		this.fifties = n50;
		/*
		 * creates a register withspecified loonies n1, fives n5, tens n10 ,
		 * twenties n20, fifties n50
		 */
	}

	/*
	 * ------------------------------------------------------------- getters
	 * -------------------------------------------------------------
	 */

	/* returns number of loonies in the register */
	public int get1() {
		return this.loonies;
	}

	/* returns number of five dollar bills in the register */
	public int get5() {
		return this.fives;
	}

	/* returns number of ten dollar bills in the register */
	public int get10() {
		return this.tens;
	}

	/* returns number of twenty dollar bills in the register */
	public int get20() {
		return this.twenties;
	}

	/* returns number of fifty dollar bills in the register */
	public int get50() {
		return this.fifties;
	}

	/* returns total value of all money in register */
	public int getTotalValue() {
		return this.loonies + this.fives * 5 + this.tens * 10 + this.twenties * 20 + this.fifties * 50;
	}

	/* returns all money in register as an array [loonies, 5s, 10s, 20s, 50s] */
	public int[] getAll() {
		return new int[] { this.loonies, this.fives, this.tens, this.twenties, this.fifties };
	}

	/* returns Money object that corresponds to all money in the register */
	public Money getMoney() {
		return new Money(this.loonies, this.fives, this.tens, this.twenties, this.fifties);
	}

	/*
	 * ------------------------------------------------------------- methods
	 * -------------------------------------------------------------
	 */
	/**
	 * Method takes as input changeRemaining/refund remaining and outputs an
	 * integer array with first element being amount of loonies needed to be
	 * refunded after subtracting available amounts of bills from the cash
	 * register to add to the change/refund, while the subsequent four elements
	 * each represent amounts of each type of bill given in the refund. Used by
	 * both purchaseItem and refundItem.
	 * 
	 * @param changeRemaining
	 * @return
	 */
	private int[] changeBills(int changeRemaining) {
		// create change int array of size 5;
		int[] change = new int[5];
		// fifth element of integer array represents number of fifties taken
		// from register, returned as change.
		// Divide change remaining by 50 (biggest bill) to determine how many
		// fifties the cash register should give
		change[4] = changeRemaining / 50;
		// if cash register has more fifties than needed to give, simply
		// subtract needed # of fifties from
		// the amount of fifties in the register.
		if (change[4] < this.fifties) {
			this.fifties -= change[4];
		}
		// if not enough fifties in the register, take all the fifties in the
		// register, and set the amount of fifties
		// taken from the register (change[4]) to how many fifties were in the
		// register.
		else {
			change[4] = this.fifties;
			this.fifties = 0;
		}
		// update how much change/ refund is still needed to be returned, by
		// subtracting the value of the fifties
		// taken from the register from the change/refund remaining variable.
		changeRemaining -= 50 * change[4];
		// continue checking how many of each type of bill is needed to be taken
		// to give change/refund using
		// the same process for the remaining bills except loonies.
		change[3] = changeRemaining / 20;
		if (change[3] < this.twenties) {
			this.twenties -= change[3];
		} else {
			change[3] = this.twenties;
			this.twenties = 0;
		}
		changeRemaining -= 20 * change[3];

		change[2] = changeRemaining / 10;
		if (change[2] < this.tens) {
			this.tens -= change[2];
		} else {
			change[2] = this.tens;
			this.tens = 0;
		}
		changeRemaining -= change[2] * 10;

		change[1] = changeRemaining / 5;
		if (change[1] < this.fives) {
			this.fives -= change[1];
		} else {
			change[1] = this.fives;
			this.fives = 0;
		}
		changeRemaining -= change[1] * 5;
		// at this point, all possible bills for each specific type of bill
		// needed for change are entered into change
		// except loonies so, change/refund remaining is now in terms of
		// loonies, which is stored into change[0].
		// change is then returned
		change[0] = changeRemaining;
		return change;
	}

	protected Money purchaseItem(Item item, Money payment) {
		/*-----------------------------------------------------------
		 * process a purchase transaction
		 * 
		 * preconditions: -item and payment are both non-null
		 * 
		 * postconditions: -if the payment was not enough for the 
		 *                  purchase returns null
		 *                 -if payment was enough then returns a money
		 *                  object with the change given (might be zero)
		 * 
		 * side effects: -if payment was enough, the money in the
		 *                cash register is updated with the price
		 *                of the transaction
		 *               -if when making change, the cash register is 
		 *                unable to make proper change, it will call
		 *                the updateMoney() method to modify its money distribution.
		   *                If it is still unable to make exact change, then 
		   *                the allLoonies() method is called.
		 *-------------------------------------------------------------------
		 */
		// calculate total payment from money object and assigning it to an int
		// variable
		int totalPay = payment.d1 + 5 * payment.d5 + 10 * payment.d10 + 20 * payment.d20 + 50 * payment.d50;
		// checks if price of item is equivalent to totalPay and if it is,
		// simply returns change as 0, a new money object
		// with 0 for all attributes after adding payment into cash register.
		if (item.getPrice() == totalPay) {
			this.loonies += payment.getAll()[0];
			this.fives += payment.getAll()[1];
			this.tens += payment.getAll()[2];
			this.twenties += payment.getAll()[3];
			this.fifties += payment.getAll()[4];
			return new Money();
		}
		// Checks if payment is greater than item price (change is needed to be
		// returned). If true,
		// calculates and returns change
		else if (item.getPrice() < totalPay) {
			// first put payment into cash register
			this.loonies += payment.getAll()[0];
			this.fives += payment.getAll()[1];
			this.tens += payment.getAll()[2];
			this.twenties += payment.getAll()[3];
			this.fifties += payment.getAll()[4];
			// initialize change remaining variable which is total value of
			// change needed to return
			int changeRemaining = totalPay - item.getPrice();
			// call changeBills and assign values into an int[] bills to get
			// amounts of bills needed for change as well as number of loonies
			// needed
			int[] bills = changeBills(changeRemaining);
			// check if amount of loonies in register are enough to give for
			// remaining change after calling changeBills.
			// If true, simply subtract number of loonies needed to be given for
			// remaining change from the register
			if (bills[0] < this.loonies) {
				this.loonies -= bills[0];
			}
			// if false, first call updateMoney, then check again if amount of
			// loonies in register is enough to give
			// back remaining change after all possible bills are already taken
			// from register (bills[0]). If true,
			// subtract amount of loonies needed for remaining change from
			// register's loonies.
			else {
				this.updateMoney();
				if (bills[0] < this.loonies) {
					this.loonies -= bills[0];
				}
				// if false, call allLoonies, then subtract amount of loonies
				// needed for change remaining from
				// the register's loonies
				else {
					this.allLoonies();
					this.loonies -= bills[0];
				}
			}
			// create new money object which represents change given, with the
			// elements of bills (int[]) assigned in
			// order to the amount of loonies,fives, etc. of the change money
			// object in the constructor
			Money change = new Money(bills[0], bills[1], bills[2], bills[3], bills[4]);
			return change;
		}
		// if the item of the price is higher than the payment given, null is
		// returned.
		else {
			return null;
		}

	}

	protected Money returnItem(Item item) {
		/*-----------------------------------------------------------
		 * return an item (giving money back) 
		 * 
		 * preconditions: -item is non-null
		 * 
		 * postconditions: -if the register has enough money to give back 
		   *                  for the item then that value is returned as
		   *                  a money object. (The money object corresponds to 
		   *                  the actual number of loonies/bill given back.)
		   *                 -otherwise, returns null.
		   * 
		 * side effects: -if the register has enough money but cannot give 
		   *                this amount exactly, it calls the updateMoney() 
		   *                method to try to give the exact value. 
		   *                If this also fails then the allLoonies() method
		   *                is called.
		   *               -the amount of money in the register after the method
		   *                is reduced by the price if the register was able to 
		   *                give this value.
		   *-------------------------------------------------------------------
		 */
		// same idea as purchaseItem, except there is no payment, and only
		// change/refund is needed to be given.
		// Refer to purchaseItem and changeBill comments.
		// Check if item price is the exact same as change in register. If true,
		// simply return cashRegister's money (object)
		// as refund/Change, and then set all attributes of cashRegister to 0
		if (this.getTotalValue() == item.getPrice()) {
			Money refund = new Money(this.loonies, this.fives, this.tens, this.twenties, this.fifties);
			this.loonies = this.fives = this.tens = this.twenties = this.fifties = 0;
			return refund;
		}
		// otherwise if cash register total money is greater than item price,
		// use same method as purchaseItem, with the
		// refundRemaining being almost the exact same as changeRemaining in
		// purchaseItem when calling changeBills()
		// to end up with money object refund, made up of bills and loonies that
		// can be given by the register
		else if (this.getTotalValue() > item.getPrice()) {

			int refundRemaining = item.getPrice();
			int[] bills = changeBills(refundRemaining);

			if (bills[0] < this.loonies) {
				this.loonies -= bills[0];
			} else {
				this.updateMoney();
				if (bills[0] < this.loonies) {
					this.loonies -= bills[0];
				} else {
					this.allLoonies();
					this.loonies -= bills[0];
				}
			}
			Money refund = new Money(bills[0], bills[1], bills[2], bills[3], bills[4]);
			return refund;
		}
		// return null if not enough money in cash register
		else {
			return null;
		}
	}

	protected CashRegister updateMoney() {
		/*----------------------------------------------------
		 * Purpose is to change the distribution of loonies/bills
		   * while keeping the total value the same.
		   * For example, 10 loonies might be exchanged for 2 five 
		   * dollar bills.
		 * 
		   * preconditions - none
		   * postconditions - returns itself (this)
		   * side effects - the distribution of loonies/bills is possibly 
		   *                changed in some way (it may not change) while
		   *                the total value remains the same
		 *-----------------------------------
		 */

		/* DO NOT CHANGE THIS LINE */
		updateMoneyCount += 1;

		/*----------------------------------------------------
		 * add your code below this comment block                                  
		 *--------------------------------------------------- */
		// Splits up one of each bill if it exists into smaller bills/loonies.
		// May not work very well for first customer
		// if variety of bills is not very high at first, but after first
		// customer there will be more and more different
		// bills and loonies in register if updateMoney is called
		if (this.fifties > 0) {
			this.twenties += 2;
			this.tens += 1;
			this.fifties--;
		}
		if (this.twenties > 0) {
			this.loonies += 10;
			this.tens++;
			this.twenties--;
		}
		if (this.tens > 0) {
			this.loonies += 5;
			this.fives++;
			this.tens--;
		}
		if (this.fives > 0) {
			this.loonies += 5;
			this.fives--;
		}
		/* DO NOT CHANGE THIS LINE */
		/* your method must return this */
		return this;
	}

	public CashRegister allLoonies() {
		/*--------------------------------------------------------------------
		 * Purpose is to change all bills in the register to loonies.
		 *
		 * preconditions - none
		 * postconditions - returns itself (this)
		 * side effects - all money in the register is changed to loonies
		 *                while the total value remains the same
		 *------------------------------------------------------------------
		 */

		/* DO NOT CHANGE THIS LINE */
		allLooniesCount += 1;

		/*----------------------------------------------------
		 * add your code below this comment block                            
		 *--------------------------------------------------- */
		this.loonies += this.fifties * 50 + this.twenties * 20 + this.tens * 10 + this.fives * 5;
		this.fifties = 0;
		this.twenties = 0;
		this.tens = 0;
		this.fives = 0;

		/* DO NOT CHANGE THIS LINE */
		/* your method must return this */
		return this;
	}

	public static void main(String[] args) {
		Item testItem = new Item("Fish", 42);
		CashRegister testCash = new CashRegister(2, 0, 0, 0, 2);
		Money test = new Money(0, 0, 0, 3, 0);
		System.out.println(java.util.Arrays.toString(testCash.purchaseItem(testItem, test).getAll()));
		System.out.println(java.util.Arrays.toString(testCash.getAll()));
		System.out.println(updateMoneyCount);
		System.out.println(allLooniesCount);
	}
}
