package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Nurse;
import com.mukesh.smarthealthmanagement.repositories.NurseRepository;
import com.mukesh.smarthealthmanagement.services.NurseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NurseServiceTest {

    @InjectMocks
    private NurseService nurseService;

    @Mock
    private NurseRepository nurseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void testCreateNurse_Success() {
        Nurse nurse = new Nurse();
        nurse.setFirstName("John");
        nurse.setLastName("Doe");
        nurse.setEmail("john.doe@example.com");
        nurse.setPhoneNumber("1234567890");
        nurse.setDepartment("Emergency");

        when(nurseRepository.save(any(Nurse.class))).thenReturn(nurse);

        Nurse createdNurse = nurseService.createNurse(nurse);

        assertNotNull(createdNurse);
        assertEquals("John", createdNurse.getFirstName());
        assertEquals("Doe", createdNurse.getLastName());
        assertEquals("john.doe@example.com", createdNurse.getEmail());
    }

    @Test
    void testCreateNurse_DataIntegrityViolation() {
        Nurse nurse = new Nurse();
        when(nurseRepository.save(any(Nurse.class))).thenThrow(new DataIntegrityViolationException("Invalid data"));

        assertThrows(IllegalArgumentException.class, () -> nurseService.createNurse(nurse));
    }

    @Test
    void testCreateNurse_UnexpectedError() {
        Nurse nurse = new Nurse();
        when(nurseRepository.save(any(Nurse.class))).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> nurseService.createNurse(nurse));
    }

    // --- Test for getAllNurses ---
    @Test
    void testGetAllNurses_Success() {
        Nurse nurse1 = new Nurse();
        Nurse nurse2 = new Nurse();
        when(nurseRepository.findAll()).thenReturn(List.of(nurse1, nurse2));

        List<Nurse> nurses = nurseService.getAllNurses();

        assertNotNull(nurses);
        assertEquals(2, nurses.size());
    }

    @Test
    void testGetAllNurses_UnexpectedError() {
        when(nurseRepository.findAll()).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> nurseService.getAllNurses());
    }

    // --- Test for getNurseById ---
    @Test
    void testGetNurseById_Success() {
        Nurse nurse = new Nurse();
        nurse.setId(1L);
        when(nurseRepository.findById(1L)).thenReturn(Optional.of(nurse));

        Nurse foundNurse = nurseService.getNurseById(1L);

        assertNotNull(foundNurse);
        assertEquals(1L, foundNurse.getId());
    }

    @Test
    void testGetNurseById_NotFound() {
        when(nurseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> nurseService.getNurseById(1L));
    }

    @Test
    void testGetNurseById_UnexpectedError() {
        when(nurseRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> nurseService.getNurseById(1L));
    }

    @Test
    void testUpdateNurse_Success() {
        // Arrange
        Nurse existingNurse = new Nurse();
        existingNurse.setId(1L);
        existingNurse.setFirstName("John");
        existingNurse.setLastName("Doe");
        existingNurse.setEmail("john.doe@example.com");
        existingNurse.setPhoneNumber("1234567890"); 
        existingNurse.setDepartment("Pediatrics");

        Nurse updatedNurse = new Nurse();
        updatedNurse.setFirstName("Jane");
        updatedNurse.setLastName("Smith");
        updatedNurse.setEmail("jane.smith@example.com");
        updatedNurse.setPhoneNumber("0987654321"); 
        updatedNurse.setDepartment("Cardiology");

        when(nurseRepository.findById(1L)).thenReturn(Optional.of(existingNurse));
        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Nurse result = nurseService.updateNurse(1L, updatedNurse);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
        assertEquals("Cardiology", result.getDepartment());

        // Capture and verify saved nurse
        ArgumentCaptor<Nurse> nurseCaptor = ArgumentCaptor.forClass(Nurse.class);
        verify(nurseRepository, times(1)).save(nurseCaptor.capture());
        Nurse savedNurse = nurseCaptor.getValue();

        // Assert that the id was preserved and updated fields were saved
        assertEquals(1L, savedNurse.getId());  // ID should remain 1
        assertEquals("Jane", savedNurse.getFirstName());
        assertEquals("Smith", savedNurse.getLastName());
        assertEquals("jane.smith@example.com", savedNurse.getEmail());
        assertEquals("0987654321", savedNurse.getPhoneNumber());
        assertEquals("Cardiology", savedNurse.getDepartment());

        verify(nurseRepository, times(1)).findById(1L);
    }
    @Test
    void testUpdateNurse_NotFound() {
        Nurse updatedNurse = new Nurse();
        when(nurseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> nurseService.updateNurse(1L, updatedNurse));
    }

    @Test
    void testUpdateNurse_InvalidData() {
        Nurse existingNurse = new Nurse();
        existingNurse.setId(1L);
        existingNurse.setFirstName("John");
        existingNurse.setLastName("Doe");
        existingNurse.setEmail("john.doe@example.com");

        Nurse updatedNurse = new Nurse();
        updatedNurse.setFirstName(""); // Invalid first name to trigger validation failure

        when(nurseRepository.findById(1L)).thenReturn(Optional.of(existingNurse));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            nurseService.updateNurse(1L, updatedNurse);
        });

        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
        assertEquals("Nurse's first name cannot be null or empty", thrown.getCause().getMessage());
    }


    // --- Test for deleteNurse ---
    @Test
    void testDeleteNurse_Success() {
        when(nurseRepository.existsById(1L)).thenReturn(true);

        nurseService.deleteNurse(1L);

        verify(nurseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNurse_NotFound() {
        when(nurseRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> nurseService.deleteNurse(1L));
    }

    @Test
    void testDeleteNurse_UnexpectedError() {
        when(nurseRepository.existsById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> nurseService.deleteNurse(1L));
    }

    // --- Test for validateNurseData ---
    @Test
    void testValidateNurseData_Valid() {
        Nurse nurse = new Nurse();
        nurse.setFirstName("John");
        nurse.setLastName("Doe");
        nurse.setEmail("john.doe@example.com");
        nurse.setPhoneNumber("1234567890");

        assertDoesNotThrow(() -> nurseService.validateNurseData(nurse));
    }

    @Test
    void testValidateNurseData_InvalidFirstName() {
        Nurse nurse = new Nurse();
        nurse.setFirstName(null);  // Invalid first name

        assertThrows(IllegalArgumentException.class, () -> nurseService.validateNurseData(nurse));
    }

    @Test
    void testValidateNurseData_InvalidLastName() {
        Nurse nurse = new Nurse();
        nurse.setLastName("");  // Invalid last name

        assertThrows(IllegalArgumentException.class, () -> nurseService.validateNurseData(nurse));
    }

    @Test
    void testValidateNurseData_InvalidEmail() {
        Nurse nurse = new Nurse();
        nurse.setEmail("invalid-email");  // Invalid email

        assertThrows(IllegalArgumentException.class, () -> nurseService.validateNurseData(nurse));
    }

    @Test
    void testValidateNurseData_InvalidPhoneNumber() {
        Nurse nurse = new Nurse();
        nurse.setPhoneNumber("12345");  // Invalid phone number

        assertThrows(IllegalArgumentException.class, () -> nurseService.validateNurseData(nurse));
    }
}
