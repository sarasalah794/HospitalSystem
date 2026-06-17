SELECT 'Patients' as Type, COUNT(*) as Total FROM patients
UNION
SELECT 'Doctors', COUNT(*) FROM doctors
UNION
SELECT 'Appointments', COUNT(*) FROM appointments
UNION
SELECT 'Medical Records', COUNT(*) FROM medical_records
UNION
SELECT 'Bills', COUNT(*) FROM bills
UNION
SELECT 'Staff', COUNT(*) FROM staff;