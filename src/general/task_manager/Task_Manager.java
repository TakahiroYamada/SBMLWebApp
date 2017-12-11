package general.task_manager;

public class Task_Manager {
	private String message;
	private String responseData;
	
	public Task_Manager( String message){
		this.message = message;
	}
	
	public String getReponseData(){
		return this.responseData;
	}
}
