
// Source: http://files.acams.org/pdfs/English_Study_Guide/Chapter_5.pdf
// Suspicious transactions which may lead to money laundering
// - Unusually high monthly balances in comparison to known sources of income.
// - Unusually large deposits, deposits in round numbers or deposits in repeated amounts that are not attributable to legitimate sources of income.
// - Multiple deposits made under reportable thresholds.
// - The timing of deposits. This is particularly important when dates of illegal payments are known.
// - Checks written for unusually large amounts (in relation to the suspect’s known practices).
// - A lack of account activity. This might indicate transactions in currency or the existence of other unknown bank accounts.
//
// Note: No specific bank models are used for this fraud transaction model class.

package amlsim.model.fraud;

import amlsim.Alert;
import amlsim.model.AbstractTransactionModel;

/**
 * Transaction model for fraudsters
 */
public abstract class FraudTransactionModel extends AbstractTransactionModel {

    // Fraud transaction model ID
    public static final int FAN_OUT = 1;
    public static final int FAN_IN = 2;
    public static final int CYCLE = 3;
    public static final int BIPARTITE = 4;
    public static final int STACK = 5;
    public static final int DENSE = 6;

    public static FraudTransactionModel getModel(int modelID, float minAmount, float maxAmount, int minStep, int maxStep){
        FraudTransactionModel model;
        switch(modelID){
            case FAN_OUT: model = new FanOutTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            case FAN_IN: model = new FanInTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            case CYCLE: model = new CycleTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            case BIPARTITE: model = new BipartiteTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            case STACK: model = new StackTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            case DENSE: model = new RandomTransactionModel(minAmount, maxAmount, minStep, maxStep); break;
            default: throw new IllegalArgumentException("Unknown fraud model ID: " + modelID);
        }
        model.setParameters(minAmount, minStep, maxStep);
        return model;
    }

    Alert alert;
    protected float minAmount;
    protected float maxAmount;
    protected int startStep;
    protected int endStep;

    public abstract void setSchedule(int modelID);

    public void setAlert(Alert ag){
        this.alert = ag;
    }

    public boolean isValidStep(long step){
        return startStep <= step && step <= endStep;
    }

    /**
     * Common constructor of fraud transaction
     * @param minAmount Mininum transaction amount
     * @param maxAmount Maximum transaction amount
     * @param startStep Start simulation step (any transactions cannot be carried out before this step)
     * @param maxStep End simulation step (any transactions cannot be carried out after this step)
     */
    public FraudTransactionModel(float minAmount, float maxAmount, int startStep, int maxStep){
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.startStep = startStep;
        this.endStep = maxStep;
    }

    /**
     * Generate a random amount
     * @return A random amount within "minAmount" and "maxAmount"
     */
    protected float getAmount(){
        return alert.getSimulator().random.nextFloat() * (maxAmount - minAmount) + minAmount;
    }

    /**
     * Generate a rounded random amount
     * @param base Multiple amount to round amount
     * @return Rounded random amount
     */
    protected float getRoundedAmount(int base){
        if(base <= 0){
            throw new IllegalArgumentException("The base must be positive");
        }
        float amount = getAmount();
        int rounded = Math.round(amount);
        return (rounded / base) * base;
    }

    /**
     * Generate a random simulation step
     * @return A simulation step within startStep and endStep
     */
    protected long getRandomStep(){
        return alert.getSimulator().random.nextInt(endStep - startStep + 1) + startStep;
    }


    /**
     *
     * @return
     */
    @Override
    public String getType() {
        return "Fraud";
    }

    @Override
    public final void sendTransaction(long step) {
    }

    public abstract void sendTransactions(long step);

}
