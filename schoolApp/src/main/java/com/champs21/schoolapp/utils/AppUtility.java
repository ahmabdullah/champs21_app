package com.champs21.schoolapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.champs21.schoolapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class AppUtility {

	public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd";
	public static final String DATE_FORMAT_APP = "dd MMM yyyy";
	public static final String DATE_FORMAT_FACEBOOK = "MM/dd/yyyy";

	private static String[] suffix = new String[] { "", "k", "m", "b", "t" };
	private static int MAX_LENGTH = 4;

	private static char[] c = new char[] { 'k', 'm', 'b', 't' };

	/**
	 * Recursive implementation, invokes itself for each factor of a thousand,
	 * increasing the class on each invokation.
	 * 
	 * @param n
	 *            the number to format
	 * @param iteration
	 *            in fact this is the class from the array c
	 * @return a String representing the number n formatted in a cool looking
	 *         way.
	 */

	public static int convertDipToPixels(Context context, float dips) {
		return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	public static String getCatNameById(Context con, int id) {
		if (id == 59)
			return "Education Changes Life";
		String[] myMenuArrayText = con.getResources().getStringArray(
				R.array.free_menus_text);
		/*
		 * String[] myMenuArrayImages = con.getResources().getStringArray(
		 * R.array.free_menus_images);
		 */
		String[] myMenuArrayIds = con.getResources().getStringArray(
				R.array.free_menus_id);
		String name = "";
		for (int i = 0; i < myMenuArrayIds.length; i++) {
			if (id == Integer.parseInt(myMenuArrayIds[i])) {
				name = myMenuArrayText[i];
				break;
			}
		}
		return name;
	}

	public static String coolFormat(double n, int iteration) {
		double d = ((long) n / 100) / 10.0;
		boolean isRound = (d * 10) % 10 == 0;// true if the decimal part is
												// equal to 0 (then it's trimmed
												// anyway)
		return (d < 1000 ? ((d > 99.9 || isRound || (!isRound && d > 9.99) ? (int) d * 10 / 10
				: d + "")
				+ "" + c[iteration])
				: coolFormat(d, iteration + 1));

	}

	/*
	 * public static String format(double number) { String r = new
	 * DecimalFormat("##0E0").format(number); r = r.replaceAll("E[0-9]",
	 * suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]); while
	 * (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) { r =
	 * r.substring(0, r.length() - 2) + r.substring(r.length() - 1); } return r;
	 * }
	 */

	public static int getProductImageWidth(Context context) {
		int density = context.getResources().getDisplayMetrics().densityDpi;
		int imageWidth = 0;
		switch (density) {
		case DisplayMetrics.DENSITY_LOW:
			imageWidth = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources()
							.getDisplayMetrics());
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			imageWidth = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources()
							.getDisplayMetrics());
			break;
		case DisplayMetrics.DENSITY_HIGH:
			imageWidth = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources()
							.getDisplayMetrics());
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			imageWidth = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources()
							.getDisplayMetrics());
			break;
		default:
			imageWidth = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources()
							.getDisplayMetrics());
			break;
		}

		return imageWidth;
	}

	public static String getMonthFullName(int monthNumber) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, monthNumber);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
		simpleDateFormat.setCalendar(calendar);
		String monthName = simpleDateFormat.format(calendar.getTime());

		return monthName;
	}

	public static int getImageResourceId(String imageName, Context context) {
		
		if (imageName != null) {
			imageName = imageName.replace("-", "_");
			if (imageName.split("\\.").length == 0)
				return R.drawable.physics;
			int resID = context.getResources().getIdentifier(
					imageName.split("\\.")[0], "drawable",
					context.getPackageName());
			if (resID == 0) {
				return R.drawable.physics;
			} else {
				return resID;
			}
		} else
			return R.drawable.physics;

	}

	public static DisplayImageOptions getOptionForUserImage() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.user_icon)
				.showImageOnFail(R.drawable.user_icon).cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .displayer(new RoundedBitmapDisplayer(5))
				.build();
		return options;
	}

	public static DisplayImageOptions getOptionForSingleImage() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.candle_normal)
				.showImageForEmptyUri(R.drawable.candle_normal)
				.showImageOnFail(R.drawable.candle_normal).cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .displayer(new RoundedBitmapDisplayer(5))
				.build();
		return options;
	}

	public static DisplayImageOptions getOption() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.user_icon)
				.showImageOnFail(R.drawable.user_icon).cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .displayer(new RoundedBitmapDisplayer(5))
				.build();
		return options;
	}

	public static int getMonthNumberFromName(String month) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("January", 1);
		hm.put("Febraury", 2);
		hm.put("March", 3);
		hm.put("April", 4);
		hm.put("May", 5);
		hm.put("June", 6);
		hm.put("July", 7);
		hm.put("August", 8);
		hm.put("September", 9);
		hm.put("October", 10);
		hm.put("November", 11);
		hm.put("December", 12);

		return hm.get(month);
	}

	public static String getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year + "";
	}

	public static String getCurrentDate(String format) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df_output = new SimpleDateFormat(format,
				java.util.Locale.getDefault());
		return df_output.format(c.getTime());
	}

	public static String getFormatedDateString(String format, Calendar c) {
		SimpleDateFormat df_output = new SimpleDateFormat(format,
				java.util.Locale.getDefault());
		return df_output.format(c.getTime());
	}

	public static String getDateString(String str, String toFormat,
			String fromFormat) {
		Date parsed = null;
		String outputDate = "";

		SimpleDateFormat df_input = new SimpleDateFormat(fromFormat,
				java.util.Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(toFormat,
				java.util.Locale.getDefault());

		try {
			parsed = df_input.parse(str);
			outputDate = df_output.format(parsed);

		} catch (ParseException e) {
			Log.e("Bal", "ParseException - dateFormat");
		}
		Log.e("Formated Date", outputDate);
		return outputDate;
	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_SERVER);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	// Internet is connected or not
	public static boolean isInternetConnected() {

		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) SchoolApp.getInstance()
				.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public static int getImageViewerImageHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int imageHeight;
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			imageHeight = (int) Math.floor((double) display.getHeight() * .4);
		else
			imageHeight = (int) Math.floor((double) display.getWidth() * .4);
		return imageHeight;

	}


    public static  IDatePickerCancel listenerDatePickerCancel;

	public static void showDateTimePicker(final String key, final String title,
			final String description, final Context context) {
		CustomDateTimePicker custom = new CustomDateTimePicker(context,
				new CustomDateTimePicker.ICustomDateTimeListener() {

					@Override
					public void onCancel() {

                        listenerDatePickerCancel.onCancelCalled();

					}

					@Override
					public void onSet(Dialog dialog, Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						// TODO Auto-generated method stub

						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");

						String dateStr = format.format(dateSelected);

						ReminderHelper.getInstance().setReminder(key, title,
								description, dateStr, context);
						Log.e("Date Selected", dateStr);
					}
				});
		/**
		 * Pass Directly current time format it will return AM and PM if you set
		 * false
		 */
		custom.set24HourFormat(false);
		/**
		 * Pass Directly current data and time to show when it pop up
		 */
		custom.setDate(Calendar.getInstance());
		custom.showDialog();
	}



    public interface IDatePickerCancel
    {
        public void onCancelCalled();
    }



	public static int getResourceImageId(int categoryId, boolean isIcon,
			boolean isWhite) {
		switch (categoryId) {
		case -1:
			return isIcon ? (isWhite ? R.drawable.home_page_banner
					: R.drawable.home_page_banner)
					: R.drawable.home_page_banner;

			// GAMES BANNERS
		case 1:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;
		case 43:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.math_games_banner;
		case 19:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;
		case 21:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;
		case 44:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;
		case 22:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;
		case 23:
			return isIcon ? (isWhite ? R.drawable.game_normal
					: R.drawable.game_tap) : R.raw.games_banner;

			// FITNESS BANNERS
		case 2:
			return isIcon ? (isWhite ? R.drawable.fitness_normal
					: R.drawable.fitness_tap) : R.raw.fitness_banner;
		case 15:
			return isIcon ? (isWhite ? R.drawable.fitness_normal
					: R.drawable.fitness_tap) : R.raw.fitness_adult;
		case 28:
			return isIcon ? (isWhite ? R.drawable.fitness_normal
					: R.drawable.fitness_tap) : R.raw.fitness_kid_banner;
		case 29:
			return isIcon ? (isWhite ? R.drawable.fitness_normal
					: R.drawable.fitness_tap) : R.raw.fitness_teenager;
		case 14:
			return isIcon ? (isWhite ? R.drawable.fitness_normal
					: R.drawable.fitness_tap) : R.raw.fitness_banner;

			// FOOD AND NUTRITION BANNERS
		case 4:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_banner;
		case 32:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_adult;
		case 26:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_kids;
		case 31:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_teenager;
		case 30:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_toddlers;
		case 45:
			return isIcon ? (isWhite ? R.drawable.food_normal
					: R.drawable.food_tap) : R.raw.food_banner;

			// ENTERTAINMENT BANNERS
		case 3:
			return isIcon ? (isWhite ? R.drawable.entertainment_normal
					: R.drawable.entertainment_tap)
					: R.raw.entertainment_banner;
		case 33:
			return isIcon ? (isWhite ? R.drawable.entertainment_normal
					: R.drawable.entertainment_tap) : R.raw.eating_out_banner;
		case 34:
			return isIcon ? (isWhite ? R.drawable.entertainment_normal
					: R.drawable.entertainment_tap) : R.raw.shoping_banner;
		case 8:
			return isIcon ? (isWhite ? R.drawable.entertainment_normal
					: R.drawable.entertainment_tap) : R.raw.movie_banner;
		case 35:
			return isIcon ? (isWhite ? R.drawable.entertainment_normal
					: R.drawable.entertainment_tap) : R.raw.tv_show_banner;

			// TRAVEL
		case 36:
			return isIcon ? (isWhite ? R.drawable.travel_normal
					: R.drawable.travel_tap) : R.raw.travel_banner;

			// SPORTS
		case 47:
			return isIcon ? (isWhite ? R.drawable.sports_normal
					: R.drawable.sports_tap) : R.raw.fitness_teenager;

			// LITERATURE
		case 48:
			return isIcon ? (isWhite ? R.drawable.literature_normal
					: R.drawable.literature_tap) : R.raw.literature_banner;

			// VIDEOS
		case 49:
			return isIcon ? (isWhite ? R.drawable.video_normal
					: R.drawable.video_tap) : R.raw.travel_banner;
			// ECA BANNERS;
		case 5:

			return isIcon ? (isWhite ? R.drawable.eca_normal
					: R.drawable.eca_tap) : R.raw.eca_banner;
		case 42:

			return isIcon ? (isWhite ? R.drawable.eca_normal
					: R.drawable.eca_tap) : R.raw.eca_adult;
		case 40:

			return isIcon ? (isWhite ? R.drawable.eca_normal
					: R.drawable.eca_tap) : R.raw.eca_kid;
		case 41:

			return isIcon ? (isWhite ? R.drawable.eca_normal
					: R.drawable.eca_tap) : R.raw.eca_teen;

			// RESOURCE CENTER
		case 6:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.resource_centar_banner;
		case 27:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.english_banner;
		case 12:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.resource_centar_banner;
		case 18:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.math_banner;
		case 20:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.scince_banner;
		case 13:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.resource_centar_banner;
		case 25:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.resource_centar_banner;
		case 46:
			return isIcon ? (isWhite ? R.drawable.resource_normal
					: R.drawable.resource_tap) : R.raw.resource_centar_banner;

			// NEWS
		case 38:
			return isIcon ? (isWhite ? R.drawable.newsarticle_normal
					: R.drawable.newsarticle_tap) : R.raw.news_banner;

			// PERSONALITY
		case 16:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.personality_tap) : R.raw.parsonality_banner;
		case 37:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.personality_tap) : R.raw.parsonality_banner;

			// Education Changes life
		case 59:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.candle_icon_red) : R.raw.edu_banner;
		case 60:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.candle_icon_red) : R.raw.edu_banner;
		case 61:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.candle_icon_red) : R.raw.edu_banner;
		case 62:
			return isIcon ? (isWhite ? R.drawable.personality_normal
					: R.drawable.candle_icon_red) : R.raw.edu_banner;
		default:
			return -1;
		}
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static float getDeviceIndependentDpFromPixel(Context context,
			float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
				context.getResources().getDisplayMetrics());
	}
}
