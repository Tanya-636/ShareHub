package com.sharehub.hub.repository;

import com.sharehub.hub.entity.File;
import com.sharehub.hub.entity.Group;
import com.sharehub.hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {


    List<File> findAllByGroupAndIsPrivateFalse(Group group);


    List<File> findAllByUploadedByAndIsPrivateTrue(User uploadedBy);


    List<File> findAllByGroupAndUploadedBy(Group group, User uploadedBy);
}