package edu.cmu.cs.vlis.timetable.obj;

/**
 * Contains task and lecture summary for one week.
 */
public class WeekSummary {
	public static class DaySummary {
		private String day;
		private String lectures;
		private String tasks;

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getLectures() {
			return lectures;
		}

		public void setLectures(String lectures) {
			this.lectures = lectures;
		}

		public String getTasks() {
			return tasks;
		}

		public void setTasks(String tasks) {
			this.tasks = tasks;
		}
	}

	private int remaining_task_num;
	private int remaining_lecture_num;
	private int passed_day;
	private DaySummary[] day_summaries;

	public int getRemaining_task_num() {
		return remaining_task_num;
	}

	public void setRemaining_task_num(int remaining_task_num) {
		this.remaining_task_num = remaining_task_num;
	}

	public int getRemaining_lecture_num() {
		return remaining_lecture_num;
	}

	public void setRemaining_lecture_num(int remaining_lecture_num) {
		this.remaining_lecture_num = remaining_lecture_num;
	}

	public DaySummary[] getDay_summaries() {
		return day_summaries;
	}

	public void setDay_summaries(DaySummary[] day_summaries) {
		this.day_summaries = day_summaries;
	}

	public int getPassed_day() {
		return passed_day;
	}

	public void setPassed_day(int passed_day) {
		this.passed_day = passed_day;
	}
}
