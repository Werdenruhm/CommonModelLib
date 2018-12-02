package CommonModelLib.objectModel.users;

import DBmethodsLib.DataRow;

/**
 *
 * 
 */
public abstract class AuthUserInfo 
{
    public final UserInfo user;
    public final byte[] user_pass;
    public AuthUserInfo(UserInfo user, byte[] user_pass) 
    {
        this.user = user;
        this.user_pass = user_pass;
    }
    public AuthUserInfo(DataRow r)
    {
        this(
            new UserInfo((Integer)r.get("user_id"), (String)r.get("login")),
            r.getNoDBNull("pass", byte[].class)
        );        
    }
    public abstract boolean isAllowedForCurrentSystem();
    public String lastTempLockId;
    public long lastTempLockIdTS;
}
