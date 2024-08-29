package com.example.superproject1.repository.item;

import com.example.superproject1.repository.item.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}