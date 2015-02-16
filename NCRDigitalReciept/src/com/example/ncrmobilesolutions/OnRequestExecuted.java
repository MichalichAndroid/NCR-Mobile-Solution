package com.example.ncrmobilesolutions;

public interface OnRequestExecuted {
	void OnRequestExecuted(String res, int requestTypeId, Object customObjectForResponse, int httpErrorCode);

}
