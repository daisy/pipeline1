#!/usr/bin/perl -w


use locale;

#**************************************************************#
# acronym_expansion
#
# Creates transcriptions for acronyms.
#
#**************************************************************#
sub acronym_expansion {
	
	my ($acronym) = shift;

#	print "AAA $acronym\n\n";


	# Remove periods
	$acronym =~ s/[\.-]//g;

	my $ending		=	0;
	my $saved_ending;
					
	# Remove final endings before lookup.
	if ( $acronym =~ s/^(.+)\:($acronym_endings)$/$1/ ) {
		$saved_ending	=	$2;
		$ending		=	1;
	}

#	print "EEE $acronym\nSSS $saved_ending\n\n";
	
	# Split at each letter
	my @acronym = split/([A-Za-z])/,$acronym;
					
	# Transcribe letters
	my @transcription = ();
	my $cv_structure;
	my $all_trk = 1;
	
	my $counter = 0;
	foreach my $a (@acronym) {

		if (exists($alphabet{$a})) {
	
			my $t = $alphabet{$a};
	
			if ( $counter != $#acronym ) {
				$t =~ s/\'//;
			}
	
			# List of transcriptions
			push @transcription,$t;
							
		} elsif ($a eq "/") {
							
			# Do nothing
							
		} elsif ($a !~ /^$/) {

			$all_trk = 0;
		}
		$counter++;
	} # end foreach $a
					
#	print "AAA $all_trk\t@transcription\n\n";	
	
	my $expansion	=	join" _ ",@transcription;
	
	if ($ending == 1) {
		($expansion,$morph) = &add_ending($expansion,$saved_ending);
		$morphology{ $curr } = $morph;
	}
	
	return $expansion;
		
} # end sub
#**************************************************************#
# add_ending
#
# Adds the saved ending to transcription.
#
#**************************************************************#
sub add_ending {
	
	my ($trans,$ending) = @_;
	my $morph;
	

	if ($ending =~ /^:?s$/) {
		$trans	.=	" s";
		$morph	=	"GEN";
		
	} elsif ($ending =~ /er$/) {
		$trans	.=	" ë r";
		$morph	=	"- PLU IND NOM";
		
	} elsif ($ending =~ /ers$/) {
		$trans	.=	" ë rs";
		$morph	=	"- PLU IND GEN";

	} elsif ($ending =~ /en$/) {
		$trans	.=	" ë n";
		$morph	=	"UTR SIN DEF NOM";

	} elsif ($ending =~ /ens$/) {
		$trans	.=	" ë n s";
		$morph	=	"UTR SIN DEF GEN";

	} elsif ($ending =~ /et$/) {
		$trans	.=	" ë t";
		$morph	=	"NEU SIN DEF NOM";

	} elsif ($ending =~ /ets$/) {
		$trans	.=	" ë t s";
		$morph	=	"NEU SIN DEF GEN";

	} elsif ($ending =~ /erna$/) {
		$trans	.=	" ë rn a";
		$morph	=	"- PLU DEF NOM";

	} elsif ($ending =~ /ernas$/) {
		$trans	.=	" ë rn a s";
		$morph	=	"- PLU DEF GEN";
	
	} elsif ($ending =~ /n$/) {
		$trans	.=	" n";
		$morph	=	"UTR SIN DEF NOM";
	
	} elsif ($ending =~ /ns$/) {
		$trans	.=	" n s";
		$morph	=	"UTR SIN DEF GEN";
	
	} elsif ($ending =~ /t$/) {
		$trans	.=	" t";
		$morph	=	"NEU SIN DEF NOM";
	
	} elsif ($ending =~ /ts$/) {
		$trans	.=	" t s";
		$morph	=	"NEU SIN DEF GEN";

	} elsif ($ending =~ /an$/) {
		$trans	.=	" a n";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR SIN DEF NOM";
		
	} elsif ($ending =~ /an$/) {
		$trans	.=	" a n";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR SIN DEF NOM";
		
	} elsif ($ending =~ /ans$/) {
		$trans	.=	" a n s";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR SIN DEF GEN";
		
	} elsif ($ending =~ /ar$/) {
		$trans	.=	" a r";
		$trans	=~	s/\'/\"/g;
		$morph	=	"- PLU IND NOM";
		
	} elsif ($ending =~ /ars$/) {
		$trans	.=	" a rs";
		$trans	=~	s/\'/\"/g;
		$morph	=	"- PLU IND GEN";
		
	} elsif ($ending =~ /arna$/) {
		$trans	.=	" a rn a";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU DEF NOM";
		
	} elsif ($ending =~ /arnas$/) {
		$trans	.=	" a rn a s";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU DEF GEN";
		
	} elsif ($ending =~ /or$/) {
		$trans	.=	" o r";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU IND NOM";
		
	} elsif ($ending =~ /ors$/) {
		$trans	.=	" o rs";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU IND NOM";
		
	} elsif ($ending =~ /orna$/) {
		$trans	.=	" o rn a";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU DEF NOM";
		
	} elsif ($ending =~ /ornas$/) {
		$trans	.=	" o rn a s";
		$trans	=~	s/\'/\"/g;
		$morph	=	"UTR PLU DEF GEN";
		
	}	
	

	return ($trans,$morph);
}
#**************************************************************#
1;
