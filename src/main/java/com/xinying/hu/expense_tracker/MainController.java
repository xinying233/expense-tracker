package com.xinying.hu.expense_tracker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.tree.Tree;
import org.apache.coyote.Response;
import org.springframework.aop.interceptor.ExposeBeanNameAdvisors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Controller // This means that this class is a Controller
@RequestMapping(path="/user") // This means URL's start with /demo (after Application path)
public class MainController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    private final MainService mainService;

    @Autowired
    private ExpenseRepository expenseRepository;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    private String checkAuthentication(Authentication authentication, User user) {
        String authenticatedUsername = authentication.getName();
        if (user == null) {
            return "not_found";
        }
        if (!user.getName().equals(authenticatedUsername)) {
            return "forbidden";
        }

        return "ok";
    }

    @GetMapping(path="/signup")
    public String signUp() {
        return "signup";
    }

    @PostMapping(path="/add") // Map ONLY POST Requests
    public String addNewUser (@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        User n = new User();
        n.setName(username);
        n.setEmail(email);
        n.setPassword(password);
        userRepository.save(n);
        return "redirect:/user/index";
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserData(@PathVariable Integer id, Authentication authentication) {
        // Retrieve authenticated user's details
        String authenticatedUsername = authentication.getName();

        // Load the user record from the database (UserService handles this)
        User user = mainService.findUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Ensure user can only access their own data
        if (!user.getName().equals(authenticatedUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping(path="index")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping(path="/{id}/expenses")
    public String showExpenses (@PathVariable Integer id, Model model, Authentication authentication) {
        User user = mainService.findUserById(id);

        String response = checkAuthentication(authentication, user);
        if (!response.equals("ok")) {
            return response;
        }

        List<Expense> expenses = expenseRepository.findAllByPayerId(id);
        expenses.addAll(expenseRepository.findAllByBorrowerId(id));
        Comparator<Expense> expenseComparator = (e1, e2) -> {
            int yearCompare = e2.getDate().getYear() - e1.getDate().getYear();
            if (yearCompare != 0) {
                return yearCompare;
            } else {
                return e2.getDate().getMonthValue() - e1.getDate().getMonthValue();
            }
        };
        expenses.sort(expenseComparator);

        List<UserRelation> relations = userRelationRepository.findAllByUserA(user);
        relations.addAll(userRelationRepository.findAllByUserB(user));

        model.addAttribute( "expenses", expenses);
        model.addAttribute("user", user);
        model.addAttribute("relations", relations);
        return "expenses";
    }

    @PostMapping(path="/{id}/expense/add")
    public String addExpense(@PathVariable Integer id, Optional<String> borrowerName, LocalDate date, float amount, float splitPercent, String category, Authentication authentication) {
        User payer = mainService.findUserById(id);
        String response = checkAuthentication(authentication, payer);
        if (!response.equals("ok")) {
            return response;
        }

        if (borrowerName.isPresent()) {
            User borrower = userRepository.findByName(borrowerName.get());
            mainService.createExpense(payer, borrower, date, amount, splitPercent, category);
        } else {
            mainService.createExpense(payer, date, amount, category);
        }
        return "redirect:/user/{id}/expenses";
    }

    @PostMapping(path="/{userId}/expense/{expenseId}/settle")
    public String settleExpense(@PathVariable Integer userId, @PathVariable Integer expenseId, Authentication authentication) {
        User user = mainService.findUserById(userId);

        String response = checkAuthentication(authentication, user);
        if (!response.equals("ok")) {
            return response;
        }

        Optional<Expense> expense = expenseRepository.findById(expenseId);
        expense.ifPresent(theExpense -> {
            if (!theExpense.isSettled()) {
                theExpense.settleExpense();
                expenseRepository.save(theExpense);

                Optional<UserRelation> relation = mainService.findUserRelationByUserIds(theExpense.getPayer(), theExpense.getBorrower());
                relation.ifPresent(theRelation -> {
                    theRelation.addToAmount(theExpense.getPayer(), (float)((-1) * 0.01 * theExpense.getAmount() * theExpense.getSplitPercent()));
                    userRelationRepository.save(theRelation);
                });
            }
        });

        return "redirect:/user/{userId}/expenses";
    }

    @PostMapping(path="/{userId}/expenses/{relatedUserId}/settle_all")
    public String settleAll(@PathVariable Integer userId, @PathVariable Integer relatedUserId, Authentication authentication) {
        User user = mainService.findUserById(userId);
        User relatedUser = mainService.findUserById(relatedUserId);

        String response = checkAuthentication(authentication, user);
        if (!response.equals("ok")) {
            return response;
        }

        List<Expense> expenses = expenseRepository.findAllByPayerIdAndBorrowerId(userId, relatedUserId);
        expenses.addAll(expenseRepository.findAllByPayerIdAndBorrowerId(relatedUserId, userId));

        Iterator<Expense> iterator = expenses.listIterator();
        while (iterator.hasNext()) { iterator.next().settleExpense(); }
        expenseRepository.saveAll(expenses);

        Optional<UserRelation> relation = mainService.findUserRelationByUserIds(user, relatedUser);
        relation.ifPresent(theRelation -> {
            theRelation.setAmount(0);
            userRelationRepository.save(theRelation);
        });
        return "redirect:/user/{userId}/expenses";
    }

    @GetMapping(path = "/{id}/visualization")
    public String visualize(@PathVariable Integer id, Model model, Authentication authentication) {
        User user = mainService.findUserById(id);
        String response = checkAuthentication(authentication, user);
        if (!response.equals("ok")) {
            return response;
        }

        List<Expense> paidExpenses = expenseRepository.findAllByPayerId(id);
        List<Expense> borrowedExpenses = expenseRepository.findAllByBorrowerId(id);

        // expense by category
        TreeMap<String, Float> mergedExpensesByCategory = new TreeMap<>();
        for(Expense expense : paidExpenses) {
            String category = expense.getCategory();
            float amount = expense.getPayerAmount();
            if (mergedExpensesByCategory.containsKey(category)) {
                mergedExpensesByCategory.put(category, mergedExpensesByCategory.get(category) + amount);
            } else {
                mergedExpensesByCategory.put(category, amount);
            }
        }
        for(Expense expense : borrowedExpenses) {
            String category = expense.getCategory();
            float amount = expense.getBorrowerAmount();
            if(mergedExpensesByCategory.containsKey((category))) {
                mergedExpensesByCategory.put(category, mergedExpensesByCategory.get(category) + amount);
            } else {
                mergedExpensesByCategory.put(category, amount);
            }
        }

        Comparator<YearMonth> yearMonthComparator = (ym1, ym2) -> {
            int yearCompare = ym2.getYear() - ym1.getYear();
            if (yearCompare != 0) {
                return yearCompare;
            } else {
                return ym2.getMonthValue() - ym1.getMonthValue();
            }
        };
        // monthly expense
        TreeMap<YearMonth, Float> mergedExpensesByMonth = new TreeMap<>(yearMonthComparator);
        for(Expense expense : paidExpenses) {
            YearMonth yearMonth = YearMonth.from(expense.getDate());
            float amount = expense.getPayerAmount();
            if (mergedExpensesByMonth.containsKey(yearMonth)) {
                mergedExpensesByMonth.put(yearMonth, mergedExpensesByMonth.get(yearMonth) + amount);
            } else {
                mergedExpensesByMonth.put(yearMonth, amount);
            }
        }
        for(Expense expense : borrowedExpenses) {
            YearMonth yearMonth = YearMonth.from(expense.getDate());
            float amount = expense.getBorrowerAmount();
            if (mergedExpensesByMonth.containsKey(yearMonth)) {
                mergedExpensesByMonth.put(yearMonth, mergedExpensesByMonth.get(yearMonth) + amount);
            } else {
                mergedExpensesByMonth.put(yearMonth, amount);
            }
        }
        // current month expense
        YearMonth currentYearMonth = YearMonth.from(LocalDate.now());
        Float currentMonthExpense = mergedExpensesByMonth.get(currentYearMonth);

        // money borrow descend
        List<UserRelation> borrowedRelations = new ArrayList<UserRelation>();
        List<UserRelation> lentRelations = new ArrayList<UserRelation>();
        for (UserRelation relation : userRelationRepository.findAllByUserA(user)) {
            if (relation.getAmount() == 0.0) {
                // settled
                continue;
            } else if (relation.getAmount() > 0) {
                // lent
                lentRelations.add(relation);
            } else {
                // borrowed
                borrowedRelations.add(relation);
            }
        }
        for (UserRelation relation : userRelationRepository.findAllByUserB((user))) {
            if (relation.getAmount() == 0.0) {
                // settled
                continue;
            } else if (relation.getAmount() > 0) {
                // borrowed
                borrowedRelations.add(relation);
            } else {
                // lent
                lentRelations.add(relation);
            }
        }


        model.addAttribute("mergedExpensesByCategory", mergedExpensesByCategory);
        model.addAttribute("mergedExpensesByMonth", mergedExpensesByMonth);
        model.addAttribute("currentMonthExpense", currentMonthExpense);
        model.addAttribute("totalBorrowedAmountByUser", borrowedRelations);
        model.addAttribute("totalLentAmountByUser", lentRelations);
        model.addAttribute("user", user);

        return "visualization";
    }
}
