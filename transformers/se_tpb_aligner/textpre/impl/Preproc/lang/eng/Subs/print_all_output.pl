#!/usr/bin/perl -w

#***************************************************************#
#	Printing all information to file			#
#								#
#	CE 070307						#
#***************************************************************#
sub print_all_output {

	
	print OUTPUT "\n---------------------------------------------------------------------------------------------------------------------\nIND\tORT\tPOS\tMOR\tTYP\tPAR\tPAN\tPHD\tDEP\tEXP\tTRA\tLAN\tPAU\tRAT\tVOI\tHOA\tHOW\n---------------------------------------------------------------------------------------------------------------------\n";
	
	# Orthography
	foreach $k (sort(keys(%orthography))) {


		$k = &format_pcounter($k);

		print OUTPUT "$k\t$orthography{$k}\t$pos{$k}\t$morphology{$k}\t$type{$k}\t$parse{$k}\t$parse_number{$k}\t$phrase_depth{$k}\t$dependency{$k}\t$exp{$k}\t$transcription{$k}\t$lang{$k}\t$pause{$k}\t$rate{$k}\t$voice{$k}\t$homo_across{$k}\t$homo_within{$k}\n\n";
	}
	
	
}
#***************************************************************#
1;
