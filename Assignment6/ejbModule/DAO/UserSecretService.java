package DAO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import exceptions.TooManyFailedTriesException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import test.GoogleCalendarService;

/**
 * UserSecretService.java
 * 
 * Implementation of the IUserSecretService interface.
 * Bean used to reset user secrets and authenticate users.
 * 
 * Should save username/password/salt to database to keep
 * track of user data.
 */
@Stateless
@javax.ejb.ApplicationException(rollback = true)
@TransactionManagement(TransactionManagementType.BEAN)
@Remote
public class UserSecretService implements IUserSecretService {
	@Resource
	private UserTransaction utx;

	@PersistenceContext(unitName = "Assignment")
	private EntityManager em;
	
	private final static int ITERATION_NUMBER = 1000;
	private final static int MAX_TRIES = 3;
	
	private String username;
	private String password;
	private String salt;
	private Map<String, Integer> userAuthTries;
	
	@PostConstruct
	private void init() {
		userAuthTries = new HashMap<String, Integer>();
	}
	
	// Returns a new encrypted password and a salt for a user.
	public void resetSecret(String username, String password) {		
        try {
        	// Uses a secure Random not a simple Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);
            // Digest computation
            byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);            
            String sDigest = new BASE64Encoder().encode(bDigest);
            String sSalt = new BASE64Encoder().encode(bSalt);
            this.username = username;
            this.password = sDigest;
            this.salt = sSalt;
            
            //System.out.println(this.username);
            //System.out.println(this.password);
            //System.out.println(this.salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	// Authenticate user with username and password. Throws TooManyFailedTriesException if
	// user provided the wrong password too many times. Returns true if successful else false.
	public boolean checkSecret(String username, String password) throws TooManyFailedTriesException {
		// Check if too many failed tries.
		// Throw exception if so.
		if (!userAuthTries.containsKey(username))
			userAuthTries.put(username, 0);
		else if (userAuthTries.get(username) >= MAX_TRIES)
			throw new TooManyFailedTriesException("");	
		
        byte[] bDigest = null;
        byte[] proposedDigest = null;
        
		try {
			bDigest = new BASE64Decoder().decodeBuffer(this.password);
			byte[] bSalt = new BASE64Decoder().decodeBuffer(this.salt);
			// Compute the new DIGEST
	        proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		boolean success = Arrays.equals(proposedDigest, bDigest);
		
		if (success)
			userAuthTries.put(username, 0);
		else userAuthTries.put(username, userAuthTries.get(username) + 1);
		
        return success;
	}
	
	// Uses the provided salt to encrypt a password with the SHA-1 algorithm.
	// Returns the hash value.
	private byte[] getHash(int iterationNr, String password, byte[] salt) {
		byte[] input = null;
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
		    digest.reset();
		    digest.update(salt);
		    input = digest.digest(password.getBytes("UTF-8"));
		    for (int i = 0; i < iterationNr; i++) {
		        digest.reset();
		        input = digest.digest(input);
		    }		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}
}
