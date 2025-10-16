package au.com.telstra.simcardactivator.dto;

public class ActivateSimResponse {
        private boolean success;
        private String message;

        public ActivateSimResponse() {}
        public ActivateSimResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
}
