package com.foferys_journal.fofejournal.models.builder;


import org.springframework.stereotype.Component;

import com.foferys_journal.fofejournal.models.User;
import com.foferys_journal.fofejournal.models.UserDto;


@Component
public class UserDtoBuilder {
    


	public static User UserFromDtoToEntity (UserDto userDto, String imageFileName, String passw) {


		User u = new User();
        u.setNome(userDto.getNome());
        u.setUsername(userDto.getUsername());
        u.setPassword(passw); 
        u.setImg(imageFileName);
		return u;
	}

	
	public static UserDto UserFromEntityToDto (User user) {
		

		UserDto uDto = new UserDto();
        uDto.setNome(user.getNome());
        uDto.setUsername(user.getUsername());
        uDto.setPassword(user.getPassword()); 
		//capire come trasformare da stringa immagine a file multipart (e se ha senso)
		// uDto.setImg(u.getImg());
		return uDto;
	}

}
