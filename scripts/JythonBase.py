from com.genomen.core import Individual
from com.genomen.scripts import LogicResult
from com.genomen.scripts import JythonLogicExecutor

jythonLogicExecutor
individual = Individual("defaultId")
defaultInterestLevel = 0

class IntermediateResult(object):
		
	def __init__(self, logicresult ):
		self.result = logicresult
		
	def __add__(self, other):
		self.result.add(other.result)
		return self
		
	def __sub__(self,other):
		self.result.subtract(other)
		return self
	
	def __mul__(self,other):		
		self.result.multiply(other.result)
		return self
		
	def __div__(self,other):
		self.result.divide(other.result)
		return self
		
	def __and__(self,other):
		self.result.and(other.result)
		return self
		
	def __or__(self,other):
		self.self.result.or(other.result)
		return self
	
	def __eq__(self,other):
		self.result.equa(other.result)	
		return self
		
	def __lt__(self,other):
		self.result.lessThan(other.result)
		return self

	def __le__(self,other):
		self.result.lessThanOrEqual(other.result)
		return self

	def __gt__(self,other):
		self.result.greaterThan(other.result)
		return self

	def __ge__(self,other):
		self.result.greaterThanOrEqual(other.result)	
		return self
		
	def risk():
		return self.result.getInterestLevel()
		
	def numeric():
		return self.result.getNumericValue()
		
	def value():
		return self.result.getValue()
		
	def getResult(self):
		return self.result

result = IntermediateResult(LogicResult())

def GENO(name,allele="", alleles = [], risk = [] ):	
	genotypeResult = LogicResult()
	
	if len(alleles) > 0 and len(risk) > 0:
		for i,a in enumerate(alleles):
			genotypeResult = jythonLogicExecutor.compareToData( "SNP",individual,name,"ALLELE",a)
			if genotypeResult.getResult():
				genotypeResult.setInterestLevel(risk[i])
			
	else:
		genotypeResult = jythonLogicExecutor.compareToData( "SNP",individual,name,"ALLELE",allele)
		genotypeResult.setType(LogicResult.ALLELE)
		
	return IntermediateResult(genotypeResult)
		
				
def RULE(id):
	ruleResult = jythonLogicExecutor.executeRule(individual,id)	
	return IntermediateResult(ruleResult)
	
def RESULT(logic, true_interest = 0, true_result = "",false_interest = 0, false_result = ""):	

	global result
	logic.getResult().setInterestLevel(defaultInterestLevel)
	if logic.getResult().getResult():
		if true_result != "":
			logic.getResult().setValue(true_result)
			
		if true_interest > 0:
			logic.getResult().setInterestLevel(true_interest)

	else:
		if false_result != "":
			logic.getResult().setValue(false_result)
			
		if false_interest > 0:
			logic.getResult().setInterestLevel(false_interest)	
		
	result = logic
			
def ACCEPT_RESULT_IF_ATLEAST_ONE_NON_MISSING_VALUE():

	for logicResult in result.getResult().getAssociatedResults():	
		if logicResult.isUnresolvable() == false:
			result.getResult().setUnresolvable(false)
			break
	
	
def ACCEPT_RESULT():

	if result.getResult().isUnresolvable():
		for logicResult in result.getResult().getAssociatedResults():	
			if logicResult.hasMissingData():
				result.getResult().setUnresolvable(false)
	
def SET_INTEREST(test,true_interest=0,false_interest=0):

	if test.getResult().getValue():
		result.getResult().setInterestLevel(true_interest)
	else:
		result.getResult().setInterestLevel(false_interest)
		
	

	

		
	

