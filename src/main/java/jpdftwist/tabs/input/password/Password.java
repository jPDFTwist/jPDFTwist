package jpdftwist.tabs.input.password;

/**
 * @author Vasilis Naskos
 */
public class Password {

    private char[] password;
    private String unlockedFilePath;
    private boolean successfullyUnlocked = true;

    public Password() {
    }

    public Password(char[] password) {
        this.password = password;
    }

    public Password(char[] password, String unlockedFilePath) {
        this.password = password;
        this.unlockedFilePath = unlockedFilePath;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }

    public String getPasswordAsString() {
        String stringPassword = new String(password);
        return stringPassword;
    }

    public void setUnlockedFilePath(String unlockedFilePath) {
        this.unlockedFilePath = unlockedFilePath;
    }

    public String getUnlockedFilePath() {
        return unlockedFilePath;
    }

    public void setSuccessfullyUnlocked(boolean successfullyUnlocked) {
        this.successfullyUnlocked = successfullyUnlocked;
    }

    public boolean isSuccessfullyUnlocked() {
        return successfullyUnlocked;
    }

}
