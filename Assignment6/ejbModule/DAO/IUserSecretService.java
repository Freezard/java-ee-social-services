package DAO;

import java.rmi.Remote;
import java.rmi.RemoteException;

import exceptions.TooManyFailedTriesException;

public interface IUserSecretService extends Remote {
	public void resetSecret(String username, String password) throws RemoteException;
	public boolean checkSecret(String username, String password) throws RemoteException, TooManyFailedTriesException;
}
