public class ContractSalaryEmployee extends SalaryEmployee {

    private double contractAmount;

    /*
    Contract employees receive a fixed monthly amount.
    */

    ContractSalaryEmployee(int id, String name, double contractAmount)
            throws InvalidEmployeeDataException {

        super(id, name);

        if (contractAmount <= 0) {
            throw new InvalidEmployeeDataException(
                    "Contract amount must be greater than 0");
        }

        this.contractAmount = contractAmount;
    }

    public double getContractAmount() {
        return contractAmount;
    }

    @Override
    public double calculateMonthlySalary() {
        return contractAmount;
    }
}