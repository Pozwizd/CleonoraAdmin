package com.example.cleonoraadmin.service.imp;


import com.example.cleonoraadmin.config.WorkScheduleConfig;
import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderCleaning;
import com.example.cleonoraadmin.entity.TimeSlot;
import com.example.cleonoraadmin.entity.Workday;
import com.example.cleonoraadmin.repository.OrderCleaningRepository;
import com.example.cleonoraadmin.repository.TimeSlotRepository;
import com.example.cleonoraadmin.repository.WorkdayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderCleaningSchedulingServiceImpTest {

    @Mock
    private WorkScheduleConfig workScheduleConfig;

    @Mock
    private WorkdayRepository workdayRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private OrderCleaningRepository orderCleaningRepository;

    @InjectMocks
    private OrderCleaningSchedulingServiceImp orderCleaningSchedulingService;

    private void setupWorkSchedule(WorkScheduleConfig workScheduleConfig) {
        Map<String, WorkScheduleConfig.WorkDay> schedule = new HashMap<>();
        schedule.put("monday", createWorkDay("09:00", "23:59", "13:00", "14:00"));
        schedule.put("tuesday", createWorkDay("09:00", "18:00", "13:00", "14:00"));
        schedule.put("wednesday", createWorkDay("09:00", "18:00", "13:00", "14:00"));
        schedule.put("thursday", createWorkDay("09:00", "18:00", "13:00", "14:00"));
        schedule.put("friday", createWorkDay("09:00", "18:00", "13:00", "14:00"));
        schedule.put("saturday", createWorkDay("off", "off", "off", "off"));
        schedule.put("sunday", createWorkDay("off", "off", "off", "off"));
        when(workScheduleConfig.getSchedule()).thenReturn(schedule);
    }

    private WorkScheduleConfig.WorkDay createWorkDay(String start, String end, String lunchStart, String lunchEnd) {
        WorkScheduleConfig.WorkDay workDay = new WorkScheduleConfig.WorkDay();
        workDay.setStart(start);
        workDay.setEnd(end);
        WorkScheduleConfig.WorkDay.Lunch lunch = new WorkScheduleConfig.WorkDay.Lunch();
        lunch.setStart(lunchStart);
        lunch.setEnd(lunchEnd);
        workDay.setLunch(lunch);
        return workDay;
    }


    @Test
    void createTimeSlotsForOrder_SimpleOrder_CreatesTimeSlots() {

        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(9, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty()); // No existing workdays


        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
        assertEquals(1, orderCleaning.getTimeSlots().size());
        TimeSlot createdSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalDate.of(2024, 3, 4), createdSlot.getDate());
        assertEquals(LocalTime.of(9, 0), createdSlot.getStartTime());
        assertEquals(LocalTime.of(11, 0), createdSlot.getEndTime());
    }

    @Test
    void getOrCreateWorkday_DayOff_ReturnsNull() {
        setupWorkSchedule(workScheduleConfig);
        LocalDate saturday = LocalDate.of(2024, 3, 9); // Saturday

        Workday workday = orderCleaningSchedulingService.getOrCreateWorkday(saturday);

        assertNull(workday);
    }

    @Test
    void findEndTime_OverlappingSlotExists_ReturnsOverlapStartTime() {

        LocalDate date = LocalDate.of(2024, 3, 4); // Monday
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime potentialEndTime = LocalTime.of(12, 0);
        LocalTime workdayEnd = LocalTime.of(18, 0);

        TimeSlot overlappingSlot = new TimeSlot();
        overlappingSlot.setStartTime(LocalTime.of(11, 0));
        overlappingSlot.setEndTime(LocalTime.of(13, 0));

        when(timeSlotRepository.findFirstByDateAndStartTimeLessThanAndEndTimeGreaterThan(date, potentialEndTime, startTime))
                .thenReturn(Optional.of(overlappingSlot));

        LocalTime endTime = orderCleaningSchedulingService.findEndTime(date, startTime, potentialEndTime, workdayEnd);

        assertEquals(LocalTime.of(11, 0), endTime);
    }

    @Test
    void createTimeSlotsForOrder_OrderSpansLunch_CreatesMultipleTimeSlots() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(11, 0)); // Before lunch
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(4)); // Spans lunch
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(2)).save(any(TimeSlot.class));
        assertEquals(2, orderCleaning.getTimeSlots().size());

        TimeSlot firstSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalDate.of(2024, 3, 4), firstSlot.getDate());
        assertEquals(LocalTime.of(11, 0), firstSlot.getStartTime());
        assertEquals(LocalTime.of(13, 0), firstSlot.getEndTime()); // Until lunch

        TimeSlot secondSlot = orderCleaning.getTimeSlots().get(1);
        assertEquals(LocalDate.of(2024, 3, 4), secondSlot.getDate());
        assertEquals(LocalTime.of(14, 0), secondSlot.getStartTime()); // After lunch
        assertEquals(LocalTime.of(16, 0), secondSlot.getEndTime());
    }

    @Test
    void createTimeSlotsForOrder_OrderStartsOnWeekend_SkipsWeekend() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 9)); // Saturday
        order.setStartTime(LocalTime.of(9, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class)); // One slot on Monday
        TimeSlot createdSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalDate.of(2024, 3, 11), createdSlot.getDate()); // Monday
        assertEquals(LocalTime.of(9, 0), createdSlot.getStartTime());
        assertEquals(LocalTime.of(11, 0), createdSlot.getEndTime());
    }

    @Test
    void createTimeSlotsForOrder_OrderSpansWeekend_SkipsWeekend() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 8)); // Friday
        order.setStartTime(LocalTime.of(17, 0)); // Near end of Friday
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(4)); // Should span to Monday
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(2)).save(any(TimeSlot.class)); // One on Friday, one on Monday
        assertEquals(2, orderCleaning.getTimeSlots().size());

        TimeSlot firstSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalDate.of(2024, 3, 8), firstSlot.getDate()); // Friday
        assertEquals(LocalTime.of(17, 0), firstSlot.getStartTime());
        assertEquals(LocalTime.of(18, 0), firstSlot.getEndTime());

        TimeSlot secondSlot = orderCleaning.getTimeSlots().get(1);
        assertEquals(LocalDate.of(2024, 3, 11), secondSlot.getDate()); // Monday
        assertEquals(LocalTime.of(9, 0), secondSlot.getStartTime());
        assertEquals(LocalTime.of(12, 0), secondSlot.getEndTime());
    }

    @Test
    void createTimeSlotsForOrder_StartTimeBeforeWorkday_AdjustsStartTime() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(8, 0)); // Before workday start
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(1));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
        TimeSlot createdSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalTime.of(9, 0), createdSlot.getStartTime()); // Adjusted to 9:00
        assertEquals(LocalTime.of(10, 0), createdSlot.getEndTime());
    }


    @Test
    void createTimeSlotsForOrder_StartTimeAfterWorkday_SkipsDay() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 5)); // Tuesday
        order.setStartTime(LocalTime.of(19, 0)); // After workday end
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        order.setOrderCleanings(List.of(orderCleaning));

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        orderCleaningSchedulingService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
        TimeSlot createdSlot = orderCleaning.getTimeSlots().get(0);
        assertEquals(LocalDate.of(2024, 3, 6), createdSlot.getDate()); // Wednesday
        assertEquals(LocalTime.of(9, 0), createdSlot.getStartTime());
        assertEquals(LocalTime.of(11, 0), createdSlot.getEndTime());
    }

    @Test
    void createTimeSlotsForOrder_InvalidSlotEndTime_DoesNotCreateSlot() {

        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(10, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        OrderCleaningSchedulingServiceImp spyService = spy(orderCleaningSchedulingService);
        doReturn(LocalTime.of(9, 0)).when(spyService).findEndTime(any(), any(), any(), any());
        doCallRealMethod().when(spyService).createTimeSlotsForOrder(any(Order.class));

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);

        spyService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, never()).save(any(TimeSlot.class)); // No slot should be created
        assertTrue(orderCleaning.getTimeSlots().isEmpty());
    }

    @Test
    void createTimeSlotsForOrder_InvalidSlotDuration_DoesNotCreateSlot() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(10, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        OrderCleaningSchedulingServiceImp spyService = spy(orderCleaningSchedulingService);
        doReturn(LocalTime.of(10, 0)).when(spyService).findEndTime(any(), any(), any(), any());
        doCallRealMethod().when(spyService).createTimeSlotsForOrder(any(Order.class));

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);

        spyService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, never()).save(any(TimeSlot.class)); // No slot should be created
        assertTrue(orderCleaning.getTimeSlots().isEmpty());
    }

    @Test
    void createTimeSlotsForOrder_NegativeSlotDuration_DoesNotCreateSlot() {

        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4)); // Monday
        order.setStartTime(LocalTime.of(10, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofHours(2));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        OrderCleaningSchedulingServiceImp spyService = spy(orderCleaningSchedulingService);
        doReturn(LocalTime.of(9, 0)).when(spyService).findEndTime(any(), any(), any(), any()); // endTime < startTime
        doCallRealMethod().when(spyService).createTimeSlotsForOrder(any(Order.class));

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);

        spyService.createTimeSlotsForOrder(order);

        verify(timeSlotRepository, never()).save(any(TimeSlot.class)); // No slot should be created
        assertTrue(orderCleaning.getTimeSlots().isEmpty());
    }

    @Test
    void createTimeSlotsForOrder_MaxIterationsReached_ThrowsException() {
        setupWorkSchedule(workScheduleConfig);

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4));
        order.setStartTime(LocalTime.of(9, 0));
        OrderCleaning orderCleaning = new OrderCleaning();
        orderCleaning.setDurationCleaning(Duration.ofDays(10));
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        OrderCleaningSchedulingServiceImp spyService = spy(orderCleaningSchedulingService);
        doReturn(LocalTime.of(9, 0).plusMinutes(1)).when(spyService).findEndTime(any(), any(), any(), any());
        doCallRealMethod().when(spyService).createTimeSlotsForOrder(any(Order.class));

        when(orderCleaningRepository.save(any(OrderCleaning.class))).thenReturn(orderCleaning);
        when(workdayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> spyService.createTimeSlotsForOrder(order));
    }

    @Test
    void calculateEndDate_ReturnsCorrectEndDate() {

        Order order = new Order();
        order.setStartDate(LocalDate.of(2024, 3, 4));
        OrderCleaning orderCleaning1 = new OrderCleaning();
        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setDate(LocalDate.of(2024, 3, 4));
        OrderCleaning orderCleaning2 = new OrderCleaning();
        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setDate(LocalDate.of(2024, 3, 5));

        orderCleaning1.setTimeSlots(List.of(timeSlot1));
        orderCleaning2.setTimeSlots(List.of(timeSlot2));
        order.setOrderCleanings(List.of(orderCleaning1, orderCleaning2));

        LocalDate endDate = orderCleaningSchedulingService.calculateEndDate(order);

        assertEquals(LocalDate.of(2024, 3, 5), endDate);
    }

    @Test
    void deleteOrderCleanings_CallsRepositoryDeleteAll() {
        Order order = new Order();
        OrderCleaning orderCleaning = new OrderCleaning();
        List<OrderCleaning> orderCleanings = new ArrayList<>();
        orderCleanings.add(orderCleaning);
        order.setOrderCleanings(orderCleanings);

        orderCleaningSchedulingService.deleteOrderCleanings(order);

        verify(orderCleaningRepository).deleteAll(orderCleanings);
    }
}
