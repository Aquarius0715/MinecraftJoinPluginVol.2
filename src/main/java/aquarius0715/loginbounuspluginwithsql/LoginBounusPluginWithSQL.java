package aquarius0715.loginbounuspluginwithsql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;


public final class LoginBounusPluginWithSQL extends JavaPlugin implements Listener {

    public static MySQLManagerV2 mysql;

    Date date = new Date();
    String format = new SimpleDateFormat("yyyy").format(date);
    int Now_Year_Int = Integer.parseInt(format);
    String format1 = new SimpleDateFormat("MM").format(date);
    int Now_Month_Int = Integer.parseInt(format1);
    String format2 = new SimpleDateFormat("dd").format(date);
    int Now_Day_Int = Integer.parseInt(format2);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        ItemStack[] items ={ new ItemStack(Material.BREAD), new ItemStack(Material.PAPER)};
        if (event.getPlayer().hasPlayedBefore()) {

            for (Player on : Bukkit.getOnlinePlayers()) {

                boolean Re_resister = false;

                on.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + event.getPlayer().getName() + "がログインしました");

                    MySQLManagerV2.Query select_UUID = mysql.query("SELECT UUID FROM basetable where UUID = '" + event.getPlayer().getUniqueId() + "';");  //エラー発生。この書き方に問題がある？
                if (select_UUID == null) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "SQLにあなたの情報がありません。再登録をします");
                    String sql = "INSERT INTO basetable (UUID, PlayerName, Year, Month, Day) VALUES ('" + event.getPlayer().getUniqueId().toString() + "' , '"
                            + event.getPlayer().getDisplayName() + "' , '"
                            + format + "' , '"
                            + format1 + "' , '"
                            + format2 + "');";
                    mysql.execute(sql);
                    event.getPlayer().getInventory().addItem(items);
                    event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "ログインボーナスを配布しました。次回は日付をまたいだ後に貰えます");
                    Re_resister = true;
                }


                    MySQLManagerV2.Query select_Year = mysql.query("SELECT Year FROM basetable where UUID = '" + event.getPlayer().getUniqueId() + "';");
                MySQLManagerV2.Query select_Month = mysql.query("SELECT Month FROM basetable where UUID = '" + event.getPlayer().getUniqueId() + "';");
                MySQLManagerV2.Query select_Day = mysql.query("SELECT Day  FROM basetable where UUID = '" + event.getPlayer().getUniqueId() + "';");

                    ResultSet result_year = select_Year.getRs();
                    result_year.last();
                    String Year = result_year.getString("Year");
                    int Year_Int = Integer.parseInt(Year);

                    ResultSet result_month = select_Month.getRs();
                    result_month.last();
                    String Month = result_month.getString("Month");
                    int Month_Int = Integer.parseInt(Month);

                    ResultSet result_day = select_Day.getRs();
                    result_day.last();
                    String Day = result_day.getString("Day");
                    int Day_Int = Integer.parseInt(Day);
                    //今回のログイン時間との差を計算しログインボーナスの配布
                    if (Now_Year_Int != Year_Int || Now_Month_Int != Month_Int || Now_Day_Int != Day_Int) {
                        event.getPlayer().getInventory().addItem(items);
                        event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "ログインボーナスを配布しました。次回は日付をまたいだ後に貰えます");
                    } else
                        event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "あなたはまだログインボーナスをもらうことができません");
                    if (Re_resister == false) {
                        //SQLにアップロード
                        String sql1 = "INSERT INTO basetable (UUID, PlayerName, Year, Month, Day) VALUES ('" + event.getPlayer().getUniqueId().toString() + "' , '"
                                + event.getPlayer().getDisplayName() + "' , '"
                                + format + "' , '"
                                + format1 + "' , '"
                                + format2 + "');";

                        mysql.execute(sql1);
                    }
                }
        } else {
            for (Player on : Bukkit.getOnlinePlayers()) {

                String select_UUID = "SELECT UUID FROM basetable where UUID = '" + event.getPlayer().getUniqueId() + "';";
                if (select_UUID.isEmpty()) {
                    //SQLにアップロード
                    String sql = "INSERT INTO basetable (UUID, PlayerName, Year, Month, Day) VALUES ('" + event.getPlayer().getUniqueId().toString() + "' , '"
                            + event.getPlayer().getDisplayName() + "' , '"
                            + format + "' , '"
                            + format1 + "' , '"
                            + format2 + "');";
                    mysql.execute(sql);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + event.getPlayer().getDisplayName() + "さんが初めてログインしました！");
                    event.getPlayer().getInventory().addItem(items);
                    event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "ログインボーナスを配布しました。次回は日付をまたいだ後に貰えます");
                }
            }
        }
    }
}
