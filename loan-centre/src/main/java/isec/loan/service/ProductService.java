package isec.loan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import isec.loan.core.AbstractService;
import isec.loan.entity.Product;


 
@Service
@Transactional
public class ProductService extends AbstractService<Product> {}
