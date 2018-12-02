package CommonModelLib.objectModel.users;

/**
 *
 * 
 */
public class UserInfo 
{
    public final int user_id; 
    public final String login; 
    public UserInfo(int user_id, String login)
    {
        this.user_id = user_id;
        this.login = login;
    }     
}
